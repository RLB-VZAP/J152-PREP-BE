/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.api;

import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.ticket.Service.TicketService;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.UUID;

/**
 *
 * @author LENOVO
 */
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

