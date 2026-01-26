package com.example.contify.global.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtProvider {
    private final Key key;
    private final long accessExpMs;
    private final long refreshExpMs;


    public JwtProvider(
            @Value("${jwt.secret}") String secret
            ,@Value("${jwt.access-token-expiration}") long accessExpMs
            ,@Value("${jwt.refresh-token-expiration}") long refreshExpMs
    ){
        this.accessExpMs = accessExpMs;
        this.refreshExpMs =refreshExpMs;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    }

    public String createToken(Long userId, String role){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+accessExpMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createToken(Long userId, String role, String type , long expMs){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+expMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String accessToken(Long userId , String role){
        return createToken(userId , role, "ACCESS", accessExpMs);
    }

    public String refreshToken(Long userId , String role){
        return createToken(userId, role, "REFRESH", refreshExpMs);
    }


    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshToken(Claims claims){
        return "REFRESH".equals(claims.get("type", String.class));
    }

    public String getRole(Claims claims){
        return claims.get("role", String.class);
    }
    public Long getUserId(Claims claims){
        return Long.valueOf(claims.getSubject());
    }
}
