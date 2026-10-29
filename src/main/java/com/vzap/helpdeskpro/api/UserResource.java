/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.api;

import com.vzap.helpdeskpro.user.model.User;
import com.vzap.helpdeskpro.user.service.UserService;
import com.vzap.helpdeskpro.commons.ServiceResponse;
import com.vzap.helpdeskpro.security.JwtService;
import com.vzap.helpdeskpro.security.Secured;
import com.vzap.helpdeskpro.user.model.Role;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status;

/**
 *
 * @author rossb
 */
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final System.Logger LOG = System.getLogger(UserResource.class.getName());

    @Inject
    private UserService userService;
    @Inject
    private JwtService jwtService;

    @POST
    @Path("/login")
    public Response login(User user) {
        try {
            User found = this.userService.login(user);
            return Response.ok(found)
                    .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(found.getUserId()))
                    .build();
        } catch (IllegalArgumentException e) {
            LOG.log(System.Logger.Level.ERROR, (String) null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            LOG.log(System.Logger.Level.ERROR, (String) null, ex);
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("The email or password credentials entered are incorrect, please try again")
                    .build();
        }
    }

    @POST
    @Path("register")
    public Response register(User user, @QueryParam("regType") String regType) {
        user.setRole(Role.valueOf(regType)); //additional validation required
        
        ServiceResponse result = this.userService.register(user);
        return switch (result.getStatus()) {
            case OK -> {
                yield Response.ok(result.getValue()).build();
            }
            case FOUND, BAD_REQUEST -> {
                yield Response.status(result.getStatus()).entity(result.getMessage()).build();
            }
            default -> {
                yield Response.status(Status.EXPECTATION_FAILED).entity("Unable to process this request at the moment").build();
            }
        };
    }
}
