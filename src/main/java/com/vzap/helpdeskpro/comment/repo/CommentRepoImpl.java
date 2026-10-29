/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.comment.repo;

/**
 *
 * @author 123iz
 */
import com.vzap.helpdeskpro.comment.model.Comment;
import com.vzap.helpdeskpro.config.DBConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Singleton
@ApplicationScoped
public class CommentRepoImpl implements CommentRepo {
    
    private static final System.Logger LOG = System.getLogger(CommentRepoImpl.class.getName());

    
    @Override
    public Optional<Comment> createComment(Comment comment) {
        
        if (comment == null || comment.getTicketId() == null 
                || comment.getAuthorId() == null
                || comment.getContent() == null 
                || comment.getContent().isBlank()) {
            
            return Optional.empty();
        }

        if (comment.getCommentId() == null) {
            comment.setCommentId(UUID.randomUUID());
        }
        
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
         
        String sql = "INSERT INTO comment (commentId, ticketId, authorId, content, createdAt) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment.getCommentId().toString());
            ps.setString(2, comment.getTicketId().toString());
            ps.setString(3, comment.getAuthorId().toString());
            ps.setString(4, comment.getContent());
            ps.setTimestamp(5, Timestamp.valueOf(comment.getCreatedAt()));

            return ps.executeUpdate() > 0 ? Optional.of(comment) : Optional.empty(); 

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to create comment", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        
        if (commentId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM comment WHERE commentId = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, commentId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapComment(rs)) : Optional.empty();
            }
            
        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to find comment", e);
            return Optional.empty();
        }

    }

    @Override
    public List<Comment> findByTicketId(UUID ticketId) {
        List<Comment> comments = new ArrayList<>();
        if (ticketId == null) {
            return comments;
        }

        String sql = "SELECT * FROM comment WHERE ticketId = ? ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ticketId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to retrieve ticket comments", e);
        }

        return comments;
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();

        String sql = "SELECT * FROM comment ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comments.add(mapComment(rs));
            }

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to retrieve comments", e);
        }
        
        return comments;
    }

    @Override
    public Optional<Comment> updateComment(Comment comment) {
        
        if (comment == null || comment.getCommentId() == null 
                || comment.getTicketId() == null
                || comment.getAuthorId() == null 
                || comment.getContent() == null 
                || comment.getCreatedAt() == null) {
            return Optional.empty();
        }
        
        String sql = "UPDATE comment SET ticketId = ?, authorId = ?, content = ?, createdAt = ? WHERE commentId = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment.getTicketId().toString());
            ps.setString(2, comment.getAuthorId().toString());
            ps.setString(3, comment.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt()));
            ps.setString(5, comment.getCommentId().toString());

            if (ps.executeUpdate() > 0) {
                return Optional.of(comment);
            }
            
        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to update comment", e);
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Comment> deleteComment(Comment comment) {
        
        if (comment == null || comment.getCommentId() == null) {
            return Optional.empty();
        }

        String sql = "DELETE FROM comment WHERE commentId = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment.getCommentId().toString());

            if (ps.executeUpdate() > 0) {
                return Optional.of(comment);
            }

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to delete comment", e);
            return Optional.empty();

        }

        return Optional.empty();
    }

    
    private Comment mapComment(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("createdAt");
        return Comment.builder()
                .commentId(UUID.fromString(rs.getString("commentId")))
                .ticketId(UUID.fromString(rs.getString("ticketId")))
                .authorId(UUID.fromString(rs.getString("authorId")))
                .content(rs.getString("content"))
                .createdAt(timestamp == null ? null : timestamp.toLocalDateTime())
                .build();
    }
}
