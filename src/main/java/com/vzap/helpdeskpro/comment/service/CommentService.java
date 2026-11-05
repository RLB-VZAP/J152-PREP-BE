/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vzap.helpdeskpro.comment.service;

/**
 *
 * @author 123iz
 */
import com.vzap.helpdeskpro.comment.model.Comment;
import com.vzap.helpdeskpro.commons.ServiceResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentService {

    Optional<Comment> createComment(Comment comment);

    Optional<Comment> findById(UUID commentId);

    List<Comment> findByTicketId(UUID ticketId);

    List<Comment> findAll();

    Optional<Comment> updateComment(Comment comment);

    Optional<Comment> deleteComment(Comment comment);
    
    /// Validates and creates a comment using the authenticated /auth supplied by the REST layer
    ServiceResponse<Comment> addComment(UUID ticketId, UUID authenticatedAuthorId, String content);

    // Returns comments only after ticket existence and access permissions can be verified
    ServiceResponse<List<Comment>> getCommentsForTicket(UUID ticketId, UUID authenticatedUserId);
}
