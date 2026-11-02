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
    public ServiceResponse<Comment> addComment(UUID ticketId,UUID authenticatedAuthorId,String content) {

        if (ticketId == null || authenticatedAuthorId == null) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST,"A valid ticket ID & User required.");
        }

        if (!isValidContent(content)) {return ServiceResponse.error(Response.Status.BAD_REQUEST,"Comment text must contain between 1 and 1000 characters.");
        }

        String normalizedContent = content.trim();

        ServiceResponse<Tickets> accessResult = findAccessibleTicket(ticketId, authenticatedAuthorId);

        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(accessResult.getStatus(),accessResult.getMessage(),accessResult.getE());
        }

        Tickets ticket = accessResult.getValue();

        if (ticket.getStatus() == null) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The ticket status not verified."
            );
        }

        if (ticket.getStatus() == Status.RESOLVED) {
            return ServiceResponse.error(Response.Status.CONFLICT,"Resolved tickets cannot receive new comments.");
        }

        Comment comment = Comment.builder()
                .commentId(UUID.randomUUID())
                .ticketId(ticketId)
                .authorId(authenticatedAuthorId)
                .content(normalizedContent)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            Optional<Comment> saved = commentRepo.createComment(comment);

            if (saved.isEmpty()) {
                return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,"The comment could not be saved.");
            }

            return ServiceResponse.success(
                    saved.get(),"Comment created successfully.");

        } catch (PersistenceException e) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,"The comment could not be saved.", e);
        }
    }

    @Override
    public ServiceResponse<List<Comment>> getCommentsForTicket(
            UUID ticketId,
            UUID authenticatedUserId) {

        if (ticketId == null || authenticatedUserId == null) {
            return ServiceResponse.error(
                    Response.Status.BAD_REQUEST,
                    "A valid ticket ID and authenticated user are required."
            );
        }

        ServiceResponse<Tickets> accessResult = findAccessibleTicket(ticketId, authenticatedUserId);

        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(
                    accessResult.getStatus(),
                    accessResult.getMessage(),
                    accessResult.getE()
            );
        }

        try {
            
            return ServiceResponse.success(commentRepo.findByTicketId(ticketId),"Comments retrieved successfully.");

        } catch (PersistenceException e) {
            return ServiceResponse.error(Response.Status.INTERNAL_SERVER_ERROR,"Comments could not be retrieved.",e);
        }
    }

    // Update an existing comment.
    @Override
    public ServiceResponse<Comment> updateComment(UUID ticketId, UUID commentId, UUID authenticatedUserId, String content) {

        if (ticketId == null || commentId == null || authenticatedUserId == null) {

            return ServiceResponse.error(
                    Response.Status.BAD_REQUEST,"Valid ticket, comment and user IDs required.");
        }

        if (!isValidContent(content)) {
            return ServiceResponse.error(
                    Response.Status.BAD_REQUEST,"Comment text must contain between 1 and 1000 characters.");
        }

        ServiceResponse<Tickets> accessResult = findAccessibleTicket(ticketId, authenticatedUserId);

        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(accessResult.getStatus(),accessResult.getMessage(),accessResult.getE());
        }

        Tickets ticket = accessResult.getValue();

        if (ticket.getStatus() == null) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,"The ticket status could not be verified.");
        }

        if (ticket.getStatus() == Status.RESOLVED) {
            return ServiceResponse.error(
                    Response.Status.CONFLICT,"Comments on resolved tickets cannot be updated.");
        }

        try {
            // Find the existing comment.
            Optional<Comment> existingResult = commentRepo.findById(commentId);

            if (existingResult.isEmpty()) {
                return ServiceResponse.error( Response.Status.NOT_FOUND,"Comment not found.");
            }

            Comment existing = existingResult.get();

            // Make sure the comment belongs to the ticket in the URL.
            if (!existing.getTicketId().equals(ticketId)) {
                return ServiceResponse.error(
                        Response.Status.NOT_FOUND,
                        "Comment not found for this ticket."
                );
            }

            // Only the original author may update the comment.
            if (!existing.getAuthorId().equals(authenticatedUserId)) {
                return ServiceResponse.error(
                        Response.Status.FORBIDDEN,
                        "You can only update your own comments."
                );
            }

            // Only update the content. Preserve all original metadata.
            existing.setContent(content.trim());

            Optional<Comment> updated =
                    commentRepo.updateComment(existing);

            if (updated.isEmpty()) {
                return ServiceResponse.error(
                        Response.Status.INTERNAL_SERVER_ERROR,
                        "The comment could not be updated."
                );
            }

            return ServiceResponse.success(
                    updated.get(),
                    "Comment updated successfully."
            );

        } catch (PersistenceException e) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "An error occurred while updating the comment.",
                    e
            );
        }
    }

    // Delete an existing comment.
    @Override
    public ServiceResponse<Comment> deleteComment(
            UUID ticketId,
            UUID commentId,
            UUID authenticatedUserId) {

        if (ticketId == null || commentId == null
                || authenticatedUserId == null) {

            return ServiceResponse.error(
                    Response.Status.BAD_REQUEST,
                    "Valid ticket, comment and user IDs are required."
            );
        }

        // Check whether the user can access the ticket.
        ServiceResponse<Tickets> accessResult =
                findAccessibleTicket(ticketId, authenticatedUserId);

        if (accessResult.getStatus() != Response.Status.OK) {
            return ServiceResponse.error(
                    accessResult.getStatus(),
                    accessResult.getMessage(),
                    accessResult.getE()
            );
        }

        // Resolved tickets cannot be modified.
        Tickets ticket = accessResult.getValue();

        if (ticket.getStatus() == null) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "The ticket status could not be verified."
            );
        }

        if (ticket.getStatus() == Status.RESOLVED) {
            return ServiceResponse.error(
                    Response.Status.CONFLICT,
                    "Comments on resolved tickets cannot be deleted."
            );
        }

        try {
            // Find the existing comment.
            Optional<Comment> existingResult =
                    commentRepo.findById(commentId);

            if (existingResult.isEmpty()) {
                return ServiceResponse.error(
                        Response.Status.NOT_FOUND,
                        "Comment not found."
                );
            }

            Comment existing = existingResult.get();

            // Make sure the comment belongs to the ticket in the URL.
            if (!existing.getTicketId().equals(ticketId)) {
                return ServiceResponse.error(
                        Response.Status.NOT_FOUND,
                        "Comment not found for this ticket."
                );
            }

            // Only the original author may delete the comment.
            if (!existing.getAuthorId().equals(authenticatedUserId)) {
                return ServiceResponse.error(
                        Response.Status.FORBIDDEN,
                        "You can only delete your own comments."
                );
            }

            Optional<Comment> deleted =
                    commentRepo.deleteComment(existing);

            if (deleted.isEmpty()) {
                return ServiceResponse.error(
                        Response.Status.INTERNAL_SERVER_ERROR,
                        "The comment could not be deleted."
                );
            }

            return ServiceResponse.success(
                    deleted.get(),
                    "Comment deleted successfully."
            );

        } catch (PersistenceException e) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the comment.",
                    e
            );
        }
    }

    // Check whether a user can access a particular ticket.
    private ServiceResponse<Tickets> findAccessibleTicket(
            UUID ticketId,
            UUID userId) {

        final Optional<Tickets> ticketResult;
        final Optional<User> userResult;

        try {
            userResult = userService.findById(userId);

            if (userResult.isEmpty() || !userResult.get().isActive()) {
                return ServiceResponse.error(
                        Response.Status.UNAUTHORIZED,
                        "The authenticated account could not be verified."
                );
            }

            ticketResult = ticketService.findById(ticketId);

            if (ticketResult.isEmpty()) {
                return ServiceResponse.error(
                        Response.Status.NOT_FOUND,
                        "Ticket not found."
                );
            }

        } catch (PersistenceException e) {
            return ServiceResponse.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Ticket access could not be verified.",
                    e
            );
        }

        Tickets ticket = ticketResult.get();
        User user = userResult.get();

        // Technicians can access any ticket.
        if (user.getRole() == Role.TECHNICIAN) {
            return ServiceResponse.success(ticket, "Access permitted.");
        }

        // Clients can only access tickets they created.
        if (user.getRole() == Role.CLIENT
                && ticket.getCreated_by() != null
                && ticket.getCreated_by().equals(userId.toString())) {

            return ServiceResponse.success(ticket, "Access permitted.");
        }

        return ServiceResponse.error(
                Response.Status.FORBIDDEN,
                "You do not have permission to access this ticket."
        );
    }

    // Validate comment content.
    private boolean isValidContent(String content) {

        return content != null
                && !content.isBlank()
                && content.trim().length() <= MAX_COMMENT_LENGTH;
    }

    // Validate a Comment object.
    private boolean isValidComment(
            Comment comment,
            boolean requireExistingFields) {

        if (comment == null
                || comment.getTicketId() == null
                || comment.getAuthorId() == null
                || !isValidContent(comment.getContent())) {

            return false;
        }

        return !requireExistingFields
                || (comment.getCommentId() != null
                && comment.getCreatedAt() != null);
    }
}
