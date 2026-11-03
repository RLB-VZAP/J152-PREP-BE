
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.security.Secured;
import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.repo.TicketRepo;
import com.vzap.helpdeskpro.ticket.repo.TicketRepoImpl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import jakarta.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    
    @Override
    public Optional<Tickets> createTicket(Tickets tickets) 
    {
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
    
    @RolesAllowed("TECHNICIAN")
    @Override
    public Tickets updateStatus(UUID ticketId, Status target, User actor){
        Tickets ticket = em.find(Tickets.class, ticketId);
        if (ticket == null) throw new NotFoundException();
        if (!ticket.getStatus().canTransitionTo(target)) throw new ClientErrorException(Response.Status.CONFLICT);
        if (ticket.getAssigned_to() == null) throw new ClientErrorException("Ticket has no assignee", Response.Status.CONFLICT);
        if (!ticket.getAssigned_to().equals(actor.getUserId())) throw new ForbiddenException();
        ticket.setStatus(target); 
        ticket.setResolved_at(Timestamp.valueOf(LocalDateTime.MIN)); 
        this.updateTicket(ticket); // the updated status goes str8 into the database.
        return ticket;
    }

    @Override
    public ServiceResponse<Tickets> getTicketDetails(UUID ticketId, UUID authenticatedUserId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public List<Tickets> findAllByClient(String client) 
    {
        return ticketRepo.findByCreator(client);
    }
}
