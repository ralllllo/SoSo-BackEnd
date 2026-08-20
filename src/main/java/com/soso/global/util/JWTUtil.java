package com.soso.global.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JWTUtil {
    
    @Value("${jwt.expiration}") 
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Algorithm alg;
    private JWTVerifier jwt;
    
    
    public JWTUtil(@Value("${jwt.secret}") String secret) { 
        this.alg = Algorithm.HMAC256(secret);
        this.jwt = JWT.require(alg).build();
    }
    
    
    public String createToken(Long user_seq, String user_type) {
        return JWT.create()
                
                .withClaim("user_seq", user_seq) 
                .withClaim("user_type", user_type)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(alg);
    }
    
    
    public String createRefreshToken(Long user_seq, String user_type) {
        return JWT.create()
                .withClaim("user_seq", user_seq) 
                .withClaim("user_type", user_type)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .sign(alg);
    }
    
    
    public DecodedJWT validation(String token) {
        return jwt.verify(token);
    }
    
    
    public Long getUserSeq(String token) {
        
        DecodedJWT decodedJWT = validation(token);
        
        
        
        return decodedJWT.getClaim("user_seq").asLong();
    }

    public String getUserType(String token) {
        DecodedJWT decodedJWT = validation(token);
        return decodedJWT.getClaim("user_type").asString();
    }
    
    
    @Deprecated
    public String getSubject(String token) {
        return validation(token).getSubject();
    }
}