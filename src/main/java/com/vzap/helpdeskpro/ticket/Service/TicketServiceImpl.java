
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.repo.TicketRepo;
import com.vzap.helpdeskpro.ticket.repo.TicketRepoImpl;
import com.vzap.helpdeskpro.user.repo.UserRepo;
import com.vzap.helpdeskpro.user.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.service.UserService;
import jakarta.inject.Inject;

import jakarta.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 *
 * @author kbser
 */
@Stateless
public class TicketServiceImpl implements TicketService 
{
    
    private static final System.Logger LOG = System.getLogger(TicketRepoImpl.class.getName());
    
    @Inject
    UserService userService;
    @Inject
    UserRepo userRepo;
    @Override
    public Optional<Tickets> createTicket(Tickets tickets) {

        if (tickets == null) {
            throw new IllegalArgumentException("Ticket information is required.");
        }

        if (tickets.getTitle() == null
                || tickets.getTitle().trim().length() < 5
                || tickets.getTitle().trim().length() > 100) {

            throw new IllegalArgumentException(
                    "Title must contain between 5 and 100 characters."
            );
        }

        if (tickets.getDescription() == null
                || tickets.getDescription().trim().length() < 10
                || tickets.getDescription().trim().length() > 2000) {

            throw new IllegalArgumentException(
                    "Description must contain between 10 and 2000 characters."
            );
        }

        if (tickets.getCategory() == null) {
            throw new IllegalArgumentException("Category is required.");
        }

        if (tickets.getPriority() == null) {
            throw new IllegalArgumentException("Priority is required.");
        }

        if (tickets.getCreated_by() == null) {
            throw new IllegalArgumentException("Authenticated user is required.");
        }

        UUID userId;

        try {
            userId = UUID.fromString(tickets.getCreated_by());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid authenticated user ID.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User does not exist.")
                );

        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is inactive.");
        }

        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException(
                    "Only clients can create support tickets."
            );
        }

        tickets.setId(UUID.randomUUID());
        tickets.setCreated_by(userId.toString());
        tickets.setAssigned_to(null);
        tickets.setStatus(Status.OPEN);
        tickets.setCreated_at(new Timestamp(System.currentTimeMillis()));
        tickets.setResolved_at(null);

        return ticketRepo.createTicket(tickets);
    }

    
    @Inject
    private TicketRepo ticketRepo;

    @Override
    public Optional<Tickets> findById(UUID ticketId) 
    {
        return ticketRepo.findById(ticketId);
    }

    @Override
    public List<Tickets> findByCategory(Category category) 
    {
        return ticketRepo.findByCategory(category);
    }

    @Override
    public List<Tickets> findByPriority(Priority priority) 
    {
        return ticketRepo.findByPriority(priority);
    }

    @Override
    public List<Tickets> findByStatus(Status status) 
    {
        return ticketRepo.findByStatus(status);
    }

    @Override
    public List<Tickets> findAll() 
    {
       return ticketRepo.findAll();
    }

    @Override
    public Optional<Tickets> updateTicket(Tickets tickets) 
    {
        return ticketRepo.updateTicket(tickets);
    }

    @Override
    public Optional<Tickets> deleteTicket(Tickets tickets) 
    {
        return ticketRepo.deleteTicket(tickets);
    }
    
    @PersistenceContext
    private EntityManager em;
    
    public Tickets updateStatus(UUID ticketId, Status target, User actor){
        Tickets ticket = em.find(Tickets.class, ticketId);
        if (ticket == null) throw new NotFoundException();
        //if (!ticket.getStatus().canTransitionTo(target)) throw new ClientErrorException(Response.Status.CONFLICT);
        if (ticket.getAssigned_to() == null) throw new ClientErrorException("Ticket has no assignee", Response.Status.CONFLICT);
        if (!ticket.getAssigned_to().equals(actor.getUserId())) throw new ForbiddenException();
        return ticket;
    }

    @Override
    public ServiceResponse<Tickets> getTicketDetails(UUID ticketId, UUID authenticatedUserId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public List<Tickets> findAllByClient(String client) 
    {
        throw new RuntimeException("Method does not exist"); //needs implementation
        // return ticketRepo.findByCreator(client)
    }
}
