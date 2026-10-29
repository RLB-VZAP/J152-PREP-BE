
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.repo.TicketRepo;
import com.vzap.helpdeskpro.ticket.repo.TicketRepoImpl;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    
}
