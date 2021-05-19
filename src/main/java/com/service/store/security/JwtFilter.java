package com.service.store.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter implements javax.servlet.Filter {

    static SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String header = httpServletRequest.getHeader("authorization");

        if (header == null ) {
            throw new ServletException("Missing or invalid Authorization header");
        } else {
            try {
                String token;
                if(header.startsWith("Bearer ")){
                    token = header.substring(7);
                }
                else{
                    token = header;
                }
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token).getBody();
                servletRequest.setAttribute("claims", claims);
            } catch (final JwtException ex) {
                throw new ServletException("Invalid token");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
