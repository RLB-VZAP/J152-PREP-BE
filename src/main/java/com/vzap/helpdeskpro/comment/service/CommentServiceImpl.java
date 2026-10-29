/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.comment.service;

/**
 *
 * @author 123iz
 */
import com.vzap.helpdeskpro.comment.model.Comment;
import com.vzap.helpdeskpro.comment.repo.CommentRepo;
import com.vzap.helpdeskpro.commons.ServiceResponse;

import com.vzap.helpdeskpro.ticket.Service.TicketService;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Business logic for creating and retrieving ticket comments.
@ApplicationScoped
public class CommentServiceImpl implements CommentService {

    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final System.Logger LOG = System.getLogger(CommentServiceImpl.class.getName());

    @Inject
    private CommentRepo commentRepo;

    @Inject
    private TicketService ticketService;

    @Inject
    private UserService userService;
    
    @Override
    public Optional<Comment> createComment(Comment comment) {
        if (!isValidComment(comment, false)) {
            return Optional.empty();
        }
        return commentRepo.createComment(comment);
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        return commentRepo.findById(commentId);
    }

    @Override
    public List<Comment> findByTicketId(UUID ticketId) {
        return commentRepo.findByTicketId(ticketId);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepo.findAll();
    }

    @Override
    public Optional<Comment> updateComment(Comment comment) {
        if (!isValidComment(comment, true)) {
            return Optional.empty();
        }
        return commentRepo.updateComment(comment);
    }

    @Override
    public Optional<Comment> deleteComment(Comment comment) {
        return commentRepo.deleteComment(comment);
    }

    @Override
    public ServiceResponse<Comment> addComment(UUID ticketId, UUID authenticatedAuthorId, String content) {
        
        if (ticketId == null || authenticatedAuthorId == null) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST,"A valid ticket ID & authenticated user required");
        }
        if (content == null || content.isBlank()) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST, "Comment text is required");
        }
        if (content.length() > MAX_COMMENT_LENGTH) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST, "Comment text cannot exceed 1000 characters.");
        }

        String normalizedContent = content.trim();

        ServiceResponse<Tickets> accessResult = findAccessibleTicket(ticketId, authenticatedAuthorId);
        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(accessResult.getStatus(), accessResult.getMessage(), accessResult.getE());
        }

        Tickets ticket = accessResult.getValue();
        if (ticket.getStatus() == null) {
            // Fail closed if the database does not provide a verifiable status.
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,
                    "The ticket status could not be verified.");
        }
        if (ticket.getStatus() == Status.RESOLVED) {
            return ServiceResponse.error(Response.Status.CONFLICT,
                    "Resolved tickets cannot receive new comments.");
        }

        Comment comment = Comment.builder()
                .commentId(UUID.randomUUID())
                .ticketId(ticketId)
                .authorId(authenticatedAuthorId)
                .content(normalizedContent)
                .createdAt(LocalDateTime.now())
                .build();

        Optional<Comment> saved;
        try {
            saved = commentRepo.createComment(comment);
        } catch (PersistenceException e) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,
                    "The comment could not be saved.", e);
        }
        if (saved.isEmpty()) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,
                    "The comment could not be saved.");
        }

        return ServiceResponse.success(saved.get(), "Comment created successfully.");
    }


    @Override
    public ServiceResponse<List<Comment>> getCommentsForTicket(UUID ticketId, UUID authenticatedUserId) {
        if (ticketId == null || authenticatedUserId == null) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST,
                    "A valid ticket ID and authenticated user are required.");
        }

        ServiceResponse<Tickets> accessResult = findAccessibleTicket(ticketId, authenticatedUserId);
        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(accessResult.getStatus(), accessResult.getMessage(), accessResult.getE());
        }

        // CommentRepoImpl orders by createdAt ASC, then commentId ASC.
        try {
            return ServiceResponse.success(commentRepo.findByTicketId(ticketId),
                    "Comments retrieved successfully.");
        } catch (PersistenceException e) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,
                    "Comments could not be retrieved.", e);
        }
    }

    /**
     * Loads both records from persistence. Authorization is based on persisted
     * user role and ticket creator; IDs and roles supplied in a request body are
     * never trusted.
     */
    private ServiceResponse<Tickets> findAccessibleTicket(UUID ticketId, UUID userId) {
        final Optional<Tickets> ticketResult;
        final Optional<User> userResult;
        try {
            userResult = userService.findById(userId); //waiting on user repo or service
            if (userResult.isEmpty() || !userResult.get().isActive()) {
                return ServiceResponse.error(Response.Status.UNAUTHORIZED, "The authenticated account could not be verified.");
            }
            ticketResult = ticketService.findById(ticketId);
            if (ticketResult.isEmpty()) {
                return ServiceResponse.error(Response.Status.NOT_FOUND, "Ticket not found.");
            }
        } catch (PersistenceException e) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR, "Ticket access not verified.", e);
        }

        Tickets ticket = ticketResult.get();
        User user = userResult.get();

        if (user.getRole() == Role.TECHNICIAN) {
            return ServiceResponse.success(ticket, "access permitted.");
        }

        if (user.getRole() == Role.CLIENT && ticket.getCreated_by() != null && ticket.getCreated_by().equals(userId.toString())) {
            return ServiceResponse.success(ticket, "access permitted.");
        }

        return ServiceResponse.error(Response.Status.FORBIDDEN,"You do not have permission to access this ticket.");
    }

    private boolean isValidComment(Comment comment, boolean requireExistingFields) {
        if (comment == null
                || comment.getTicketId() == null
                || comment.getAuthorId() == null
                || comment.getContent() == null
                || comment.getContent().isBlank()
                || comment.getContent().length() > MAX_COMMENT_LENGTH) {
            return false;
        }
        return !requireExistingFields
                || (comment.getCommentId() != null && comment.getCreatedAt() != null);
    }
}
