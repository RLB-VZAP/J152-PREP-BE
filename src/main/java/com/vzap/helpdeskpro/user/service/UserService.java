/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vzap.helpdeskpro.user.service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.user.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author rossb
 */
public interface UserService {
    User login(User user)throws Exception;
    ServiceResponse<User> register(User user);
    Optional<User> findById(UUID userId);
}
