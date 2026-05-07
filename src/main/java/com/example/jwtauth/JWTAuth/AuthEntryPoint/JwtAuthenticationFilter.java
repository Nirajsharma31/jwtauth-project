package com.example.jwtauth.JWTAuth.AuthEntryPoint;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");

        logger.info("Authorization Header: {}", requestHeader);

        String username = null;
        String token = null;

        // ==============================
        // Extract Token
        // ==============================
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {

            token = requestHeader.substring(7);

            try {
                username = jwtHelper.getUsernameFromToken(token);

            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("Invalid Token");
                return;

            } catch (ExpiredJwtException e) {
                logger.error("JWT token expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Token Expired");
                return;

            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT token");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid Token");
                return;

            } catch (Exception e) {
                logger.error("JWT Error", e);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Authentication Failed");
                return;
            }

        } else {
            logger.warn("Authorization header missing or invalid");
        }

        // ==============================
        // Validate Token & Authenticate
        // ==============================
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtHelper.validateToken(token, userDetails)) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                logger.error("Token validation failed");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid Token");
                return;
            }
        }

        // ==============================
        // Continue Request
        // ==============================
        filterChain.doFilter(request, response);
    }
}