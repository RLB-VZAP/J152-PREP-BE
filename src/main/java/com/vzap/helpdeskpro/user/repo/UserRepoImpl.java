/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.user.repo;

import com.vzap.helpdeskpro.config.DBConfig;
import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
    
    private Optional<User> findOne(String sql, String... values) {
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
       
            }
        } catch (SQLException | IllegalArgumentException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to retrieve user", e);
            throw new PersistenceException("Unable to retrieve user.", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(UUID.fromString(rs.getString("userId")))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("passwordHash"))
                .fullName(rs.getString("fullName"))
                .role(Role.valueOf(rs.getString("role")))
                .active(rs.getBoolean("active"))
                .build();
    }

    @Override
    public Optional<User> createUser(User user) {
        
        if (user == null 
                || user.getUserId() == null 
                || isBlank(user.getEmail())
                || isBlank(user.getPasswordHash()) 
                || isBlank(user.getFullName()) 
                || user.getRole() == null) {
            return Optional.empty();
        }
        
        user.setActive(true);
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
        if (userId == null) {
            return Optional.empty();
        }
        return findOne("SELECT * FROM user WHERE userId = ?", userId.toString());
    }

    @Override
    public Optional<User> findByEmailAndPassword(User user) {
         if (user == null 
                 || isBlank(user.getEmail()) 
                 || isBlank(user.getPasswordHash())) {
            return Optional.empty();
        }
        
        return findOne("SELECT * FROM user WHERE email = ? AND passwordHash = ?",
                user.getEmail(), user.getPasswordHash());
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
        
      List<User> users = new ArrayList<>();
        if (role == null) {
            return users;
        }

        String sql = "SELECT * FROM user WHERE role = ? ORDER BY fullName ASC, userId ASC";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
            
        } catch (SQLException | IllegalArgumentException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to retrieve users by role", e);
        }
        return users;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY fullName ASC, userId ASC";
        try (Connection con = DBConfig.getCon();PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException | IllegalArgumentException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to retrieve users", e);
        }
        return users;
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (user == null || user.getUserId() == null 
                || isBlank(user.getEmail())
                || isBlank(user.getPasswordHash()) 
                || isBlank(user.getFullName()) 
                || user.getRole() == null) {
            return Optional.empty();
        }

        String sql = "UPDATE user SET email = ?, passwordHash = ?, fullName = ?, role = ?, active = ? WHERE userId = ?";
        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setBoolean(5, user.isActive());
            ps.setString(6, user.getUserId().toString());
            return ps.executeUpdate() > 0 ? Optional.of(user) : Optional.empty();
        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to update user", e);
            return Optional.empty();
        }
    }


    @Override
    public Optional<User> deleteUser(User user) {
       
        if (user == null || user.getUserId() == null) {
            return Optional.empty();
        }

        String sql = "DELETE FROM user WHERE userId = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user.getUserId().toString());
            return ps.executeUpdate() > 0 ? Optional.of(user) : Optional.empty();
        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, "Unable to delete user", e);
            return Optional.empty();
        }
    }
}
