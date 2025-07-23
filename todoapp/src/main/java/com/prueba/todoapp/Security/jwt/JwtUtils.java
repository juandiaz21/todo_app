package com.prueba.todoapp.Security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JwtUtils {
    
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    //Crear token
    public String generateAccesToken(String username){
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
            .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
            .setSigningKey(getSignatureKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsFunction){
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }


    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
