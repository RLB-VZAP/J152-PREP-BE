/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.user.service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.repo.UserRepo;
import com.vzap.helpdeskpro.util.HashUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author rossb
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepo userRepo;

    @Override
    public User login(User user) throws Exception {
        User dbUser = this.userRepo.findByEmail(user.getEmail()).orElseThrow(IllegalArgumentException::new);
        if (HashUtil.verifyHash(user.getPasswordHash(), dbUser.getPasswordHash())) {
            return dbUser;
        }
        throw new Exception();
    }

    @Override
    public ServiceResponse<User> register(User user) {
        Optional<User> exists = userRepo.findByEmail(user.getEmail());
        
        if (exists.isPresent()) {
            return ServiceResponse.error(Response.Status.FOUND, "You already have an account registered, please login");
        }
        
        user.setUserId(UUID.randomUUID());
        user.setPasswordHash(HashUtil.hashString(user.getPasswordHash()));
        
        try {
            return ServiceResponse.success(this.userRepo.createUser(user).orElseThrow(), "Registration successful");
        } catch (Exception e) {
            return ServiceResponse.error(Response.Status.BAD_REQUEST, "Something went wrong during registration, please try again later");
        }
    }
    
    @Override
    public List<User>getActiveTechs(){
        List<User>techs = userRepo.findByRole(Role.TECHNICIAN),
                activeTechs = new ArrayList<>();
        
        for(User user: techs){
            if(user.isActive() == true){
                activeTechs.add(user);
            }
        }
        
        return activeTechs;
        
    }

}
