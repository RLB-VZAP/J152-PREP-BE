/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vzap.helpdeskpro.user.repo;

import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author rossb
 */
public interface UserRepo {
    Optional<User> createUser(User user);
    Optional<User> findById(UUID userId);
    Optional<User> findByEmailAndPassword(User user);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findAll();
    Optional<User> updateUser(User user);
    Optional<User> deleteUser(User user);
}
