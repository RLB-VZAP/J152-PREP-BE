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
import jakarta.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CommentRepoImpl implements CommentRepo {

    @Override
    public Optional<Comment> createComment(Comment comment) {
        String sql = "INSERT INTO comment "
                + "(commentId, ticketId, authorId, content, createdAt) "
                + "VALUES (?, ?, ?, ?, ?)";

        if (comment.getCommentId() == null) {
            comment.setCommentId(UUID.randomUUID());
        }

        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment.getCommentId().toString());
            ps.setString(2, comment.getTicketId().toString());
            ps.setString(3, comment.getAuthorId().toString());
            ps.setString(4, comment.getContent());
            ps.setTimestamp(5, Timestamp.valueOf(comment.getCreatedAt()));

            if (ps.executeUpdate() > 0) {
                return Optional.of(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        String sql = "SELECT * FROM comment WHERE commentId = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, commentId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapComment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Comment> findByTicketId(UUID ticketId) {
        List<Comment> comments = new ArrayList<>();

        String sql = "SELECT * FROM comment WHERE ticketId = ? "
                + "ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ticketId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public Optional<Comment> updateComment(Comment comment) {
        String sql = "UPDATE comment SET ticketId = ?, authorId = ?, "
                + "content = ?, createdAt = ? WHERE commentId = ?";

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
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Comment> deleteComment(Comment comment) {
        String sql = "DELETE FROM comment WHERE commentId = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, comment.getCommentId().toString());

            if (ps.executeUpdate() > 0) {
                return Optional.of(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        return Comment.builder()
                .commentId(UUID.fromString(rs.getString("commentId")))
                .ticketId(UUID.fromString(rs.getString("ticketId")))
                .authorId(UUID.fromString(rs.getString("authorId")))
                .content(rs.getString("content"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .build();
    }
}
