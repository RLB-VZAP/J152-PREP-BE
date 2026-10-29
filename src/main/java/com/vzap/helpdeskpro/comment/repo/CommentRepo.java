/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vzap.helpdeskpro.comment.repo;

/**
 *
 * @author 123iz
 */
import com.vzap.helpdeskpro.comment.model.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepo {

    Optional<Comment> createComment(Comment comment);

    Optional<Comment> findById(UUID commentId);

    List<Comment> findByTicketId(UUID ticketId);

    List<Comment> findAll();

    Optional<Comment> updateComment(Comment comment);

    Optional<Comment> deleteComment(Comment comment);
}
