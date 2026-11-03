
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.repo.TicketRepo;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.vzap.helpdeskpro.ticket.repo.TicketRepoImpl;


/**
 *
 * @author kbser
 */

public class TicketServiceImpl implements TicketService 
{
    private static final System.Logger LOG = System.getLogger(TicketRepoImpl.class.getName());

    @Override
    public Optional<Tickets> createTicket(Tickets tickets) 
    {
         if(tickets == null
            || tickets.getTitle() == null
            || tickets.getTitle().isBlank()
            || tickets.getDescription() == null
            || tickets.getDescription().isBlank())
         {
             return Optional.empty();
         }
        
         tickets.setStatus(Status.OPEN);
         TicketRepoImpl ticketRepoImpl = new TicketRepoImpl();
         
       return ticketRepoImpl.createTicket(tickets);
    }
    
    @Inject
    private TicketRepoImpl ticketRepoImpl;

    @Override
    public Optional<Tickets> findById(UUID ticketId) 
    {
        return ticketRepoImpl.findById(ticketId);
    }

    @Override
    public List<Tickets> findByCategory(Category category) 
    {
        return ticketRepoImpl.findByCategory(category);
    }

    @Override
    public List<Tickets> findByPriority(Priority priority) 
    {
        return ticketRepoImpl.findByPriority(priority);
    }

    @Override
    public List<Tickets> findByStatus(Status status) 
    {
        return ticketRepoImpl.findByStatus(status);
    }

    @Override
    public List<Tickets> findAll() 
    {
       return ticketRepoImpl.findAll();
    }

    @Override
    public Optional<Tickets> updateTicket(Tickets tickets) 
    {
        return ticketRepoImpl.updateTicket(tickets);
    }

    @Override
    public Optional<Tickets> deleteTicket(Tickets tickets) 
    {
        return ticketRepoImpl.deleteTicket(tickets);
    }
    
}
