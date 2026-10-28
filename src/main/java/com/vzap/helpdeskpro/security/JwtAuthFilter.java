/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.security;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 *
 * @author rossb
 */
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter{
    
    @Inject
    private JwtService jwtService;
    
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        String authHeader = request.getHeaderString(
                HttpHeaders.AUTHORIZATION
        );
        
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            reject(request);
            return;
        }
        
        String token = authHeader.substring(7).trim();
        
        if(token.isEmpty()){
            reject(request);
            return;
        }
        
        try{
            String userId = jwtService.getUserId(token).toString();
            request.setProperty("userId", userId);
        }catch(JwtException | IllegalArgumentException e){
            reject(request);
        }
    }

    private void reject(ContainerRequestContext request) {
        request.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .entity("Invalid or expired JWT token")
                .build()
        );
    }
    
}
