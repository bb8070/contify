package com.example.contify.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    public final AccessTokenBlacklistService blacklistService;
    public JwtAuthenticationFilter(JwtProvider jwtProvider , AccessTokenBlacklistService blacklistService){
        this.jwtProvider = jwtProvider;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            try{

                if(blacklistService.isBlackListed(token)){
                    throw new RuntimeException("Blacklisted access token");
                }

                Claims claims = jwtProvider.parseClaims(token);
                Long userId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                userId , null,
                                List.of(new SimpleGrantedAuthority("ROLE_"+role))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch(Exception e){

            }

        }
        filterChain.doFilter(request, response);
    }
}
