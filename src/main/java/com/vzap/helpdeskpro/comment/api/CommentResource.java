/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.comment.api;

/**
 *
 * @author 123iz
 */
import com.vzap.helpdeskpro.comment.model.Comment;
import com.vzap.helpdeskpro.comment.service.CommentService;
import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.security.Secured;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.*;

//REST endpoints for comments on a ticket.
@Path("/tickets/{ticketId}/comments")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

    @Inject
    private CommentService commentService;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response getComments(@PathParam("ticketId") String ticketIdValue) {
        UUID ticketId = parseUuid(ticketIdValue);
        if (ticketId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ticket ID.").build();
        }

        UUID userId = getAuthenticatedUserId();
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication is required.").build();
        }

        ServiceResponse<List<Comment>> result = commentService.getCommentsForTicket(ticketId, userId);
        if (result.getStatus() != Response.Status.OK) {
            return Response.status(result.getStatus()).entity(result.getMessage()).build();
        }
        return Response.ok(result.getValue()).build();
    }

    @POST
    public Response createComment(@PathParam("ticketId") String ticketIdValue, CommentRequest request) {
        UUID ticketId = parseUuid(ticketIdValue);
        if (ticketId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ticket ID.").build();
        }
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("A comment body is required.").build();
        }

        UUID userId = getAuthenticatedUserId();
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication is required.").build();
        }

        ServiceResponse<Comment> result = commentService.addComment(ticketId, userId, request.getContent());
        if (result.getStatus() != Response.Status.OK) {
            return Response.status(result.getStatus()).entity(result.getMessage()).build();
        }

        Comment saved = result.getValue();
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    private UUID getAuthenticatedUserId() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
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

