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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
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

    
    @Override
    public Optional<Comment> createComment(Comment comment) {
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

        /*
         * TODO HD-16 — Ticket status validation (currently unavailable on dev):
         * Once Karabo's Ticket model/repository/service is merged, 
         * TODO HD-16 — Ticket access authorization (currently unavailable on dev):
         * 
         */
        
        return ServiceResponse.error(Response.Status.SERVICE_UNAVAILABLE, "Comment creation is unavailable waiting on HD-03 karabo ticket");

        /* Example integration once the Ticket service is available:
        if (!canUserAccessTicket(ticketId, authenticatedAuthorId)) {
            return ServiceResponse.error(Response.Status.FORBIDDEN, "You cannot access this ticket.");
        }
        if (!canCommentOnTicket(ticketId)) {
            return ServiceResponse.error(Response.Status.CONFLICT, "Resolved tickets cannot receive comments.");
        }
        ....
    
        */
    }

    @Override
    public ServiceResponse<List<Comment>> getCommentsForTicket(UUID ticketId, UUID authenticatedUserId) {
        
        if (ticketId == null || authenticatedUserId == null) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST, "A valid ticket ID and authenticated user required.");
        }

        /*
         * TODO HD-16 — Before returning comments, 
         * Return 404 for an unknown ticket and 403 when access is denied. Do not
         * expose comments if ticket access cannot be verified.
         */
        return ServiceResponse.error(Response.Status.SERVICE_UNAVAILABLE,"Comment retrieval is is unavailable waiting on HD-03 karabo ticket");

        /* Example integration once the Ticket service is available:
        if (!canUserAccessTicket(ticketId, authenticatedUserId)) {
            return ServiceResponse.error(Response.Status.FORBIDDEN, "You cannot access this ticket.");
        }
        return ServiceResponse.success(commentRepo.findByTicketId(ticketId), "Comments retrieved.");
        */
    }
}

