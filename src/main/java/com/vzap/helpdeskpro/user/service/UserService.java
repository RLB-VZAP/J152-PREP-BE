/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vzap.helpdeskpro.user.service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.user.model.User;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author rossb
 */
public interface UserService {
    User login(User user)throws Exception;
    ServiceResponse<User> register(User user);
    
    //list of active technicians
    TreeMap<Integer,User>getActiveTechs();
    
    //list of Open Tickets
    TreeMap<Integer,Tickets>getOpenTickets();
    
    //assign ticket
    void assignTicket(User tech, Tickets ticket);
    
    //get chosen ticket
    Optional<Tickets>chosenTicket(int choice);
    
    //get chosen tech
    Optional<User>chosenTechnician(int choice);
    Optional<User> findById(UUID userId);
}
