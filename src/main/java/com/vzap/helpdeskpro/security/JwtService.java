/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

/**
 *
 * @author rossb
 */
@ApplicationScoped
public class JwtService {
    private static final String ISSUER = "helpdeskpro";
    private static final long EXPIRATION_SECONDS = 3600;
    private final SecretKey key;
    
    public JwtService(){
        String secret = "dC9unzRCCbK4uSj3goK+f9MiwcdCB+zmw9fTTxDFao8="; //replace with environment variable
        
        if(secret == null || secret.isBlank()){
            throw new IllegalStateException("Unable to load JWT_SECRET environment variable");
        }
        
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        
        if(keyBytes.length < 32){
            throw new IllegalStateException("JWT SECRET MUST BE ATLEAST 32 BYTES LONG");
        }
        
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(UUID userId){
        Instant now = Instant.now();
        
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(EXPIRATION_SECONDS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
    
    public UUID getUserId(String token){
        Claims claims = validateToken(token);
        
        if(claims.getSubject() == null || claims.getSubject().isBlank()){
            throw new JwtException("Missing user subject");
        }
        
        return UUID.fromString(claims.getSubject());
        
    }

    private Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    
}
