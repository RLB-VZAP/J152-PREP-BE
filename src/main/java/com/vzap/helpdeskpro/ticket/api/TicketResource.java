/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.api;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.security.Secured;
import com.vzap.helpdeskpro.ticket.Service.TicketService;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Optional;
import java.util.UUID;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Category;
/**
 *
 * @author LENOVO
 */
public class TicketResource {
    
    @Inject
    private TicketService ticketService;
    private Priority priority;
    private Category category;

    @Context
    private SecurityContext securityContext;
    
    @Path("/tickets")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

    

    @POST
    public Response createTicket(TicketRequest request) {

        try {

            if (request == null || request.getTitle() == null || request.getDescription() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ticket information is required.").build();
            }
            
            if(!(request.getPriority() instanceof Priority)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Ticket priority is not valid. Can only be  LOW, MEDIUM, HIGH").build();
            }
            
            if (!(request.getCategory() instanceof Category)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Ticket category is not valid. Can only be HARDWARE, SOFTWARE, NETWORK, OTHER").build();
            }

            if (securityContext.getUserPrincipal()== null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication required.").build();
            }

            String userId = getAuthenticatedUserId().toString() ;
            
            if(!(userId == null || userId.isEmpty())){
            
                Tickets ticket = Tickets.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .category(request.getCategory())
                        .priority(request.getPriority())
                        .created_by(userId)
                        .build();
                
                Optional<Tickets> result= ticketService.createTicket(ticket);
                
                if (result.isPresent()) {
                    return Response.status(Response.Status.CREATED)
                            .entity(result.get())
                            .build();
                }
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to create ticket.")
                    .build();

        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while creating the ticket.")
                    .build();
        }
    }

    @GET
    @Path("/{ticketId}")
    public Response getTicketDetails(@PathParam("ticketId") String ticketIdValue) {

        UUID ticketId = parseUuid(ticketIdValue);

        if (ticketId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ticket ID.")
                    .build();
        }

        UUID userId = getAuthenticatedUserId();

        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authentication is required.")
                    .build();
        }

        ServiceResponse<Tickets> result =
                ticketService.getTicketDetails(ticketId, userId);

        if (result.getStatus() != Response.Status.OK) {
            return Response.status(result.getStatus())
                    .entity(result.getMessage())
                    .build();
        }

        return Response.ok(result.getValue()).build();
    }

    private UUID getAuthenticatedUserId() {

        if (securityContext == null ||
                securityContext.getUserPrincipal() == null) {
            return null;
        }

        return parseUuid(securityContext.getUserPrincipal().getName());
    }

    private UUID parseUuid(String value) {

        try {
            return value == null ? null : UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
}

