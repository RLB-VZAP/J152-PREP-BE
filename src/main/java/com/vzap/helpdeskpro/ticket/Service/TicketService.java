/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author kbser
 */
public interface TicketService 
{
    Optional<Tickets> createTicket(Tickets tickets);
    Optional<Tickets> findById(UUID ticketId);
    List<Tickets> findByCategory(Category category);
    List<Tickets> findByPriority(Priority priority);
    List<Tickets> findByStatus(Status status);
    List<Tickets> findAll();
    Optional<Tickets> updateTicket(Tickets tickets);
    Optional<Tickets> deleteTicket(Tickets tickets);
}
