/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.user.repo;

import com.vzap.helpdeskpro.config.DBConfig;
import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author rossb
 */
@ApplicationScoped
public class UserRepoImpl implements UserRepo {

    private static final System.Logger LOG = System.getLogger(UserRepoImpl.class.getName());

    @Override
    public Optional<User> createUser(User user) {
        String query = "INSERT INTO USER(userId, email, passwordHash, fullName, role) VALUES(?,?,?,?,?)";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getRole().name());
            
            if(ps.executeUpdate() > 0){
                user.setActive(true);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<User> findByEmailAndPassword(User user) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            User.builder()
                                    .email(email)
                                    .fullName(rs.getString("fullName"))
                                    .passwordHash(rs.getString("passwordHash"))
                                    .userId(UUID.fromString(rs.getString("userId")))
                                    .role(Role.valueOf(rs.getString("role")))
                                    .active(rs.getBoolean("active"))
                                    .build()
                    );
                }
            }

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findByRole(Role role) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<User> updateUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<User> deleteUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
