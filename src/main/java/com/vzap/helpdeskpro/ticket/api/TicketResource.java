/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.api;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.security.Secured;
import com.vzap.helpdeskpro.ticket.Service.TicketService;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import com.vzap.helpdeskpro.ticket.model.UpdateStatusRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.UUID;

/**
 *
 * @author LENOVO
 */
@Path("/ticket")
@Secured
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {
    
    @Inject
    private TicketService ticketService;

    @Context
    private SecurityContext securityContext;

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
    
    @GET
    @Path("api/{ticketId}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTicketStatus(@PathParam("ticketId") UUID ticketId, 
            UpdateStatusRequest request
    ){
        Tickets updatedTicket = ticketService.updateStatus(
                ticketId, 
                request.getTarget(), 
                request.getActor()
        );
        return Response.ok(updatedTicket).build();
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

