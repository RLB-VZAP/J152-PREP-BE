
package com.vzap.helpdeskpro.ticket.Service;

import com.vzap.helpdeskpro.user.service.UserService;
import jakarta.inject.Inject;
import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.repo.TicketRepo;
import com.vzap.helpdeskpro.ticket.repo.TicketRepoImpl;
import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.user.model.Role;
import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 *
 * @author kbser
 */
@ApplicationScoped
public class TicketServiceImpl implements TicketService 
{
    private static final System.Logger LOG = System.getLogger(TicketRepoImpl.class.getName());
    @Inject
    private UserService userService;

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
    
    @Override
public ServiceResponse<Tickets> getTicketDetails(UUID ticketId, UUID authenticatedUserId) {

    if (ticketId == null || authenticatedUserId == null) {
        return ServiceResponse.error(
                Response.Status.BAD_REQUEST,
                "A valid ticket ID and authenticated user are required."
        );
    }

    Optional<User> userResult = userService.findById(authenticatedUserId);

    if (userResult.isEmpty() || !userResult.get().isActive()) {
        return ServiceResponse.error(
                Response.Status.UNAUTHORIZED,
                "The authenticated account could not be verified."
        );
    }

    Optional<Tickets> ticketResult = ticketRepo.findById(ticketId);

    if (ticketResult.isEmpty()) {
        return ServiceResponse.error(
                Response.Status.NOT_FOUND,
                "Ticket not found."
        );
    }

    User user = userResult.get();
    Tickets ticket = ticketResult.get();

    if (user.getRole() == Role.TECHNICIAN) {
        return ServiceResponse.success(ticket, "Ticket retrieved successfully.");
    }

    if (user.getRole() == Role.CLIENT
            && ticket.getCreated_by() != null
            && ticket.getCreated_by().equals(authenticatedUserId.toString())) {

        return ServiceResponse.success(ticket, "Ticket retrieved successfully.");
    }

    return ServiceResponse.error(
            Response.Status.FORBIDDEN,
            "You do not have permission to access this ticket."
    );
}

    
    
}
