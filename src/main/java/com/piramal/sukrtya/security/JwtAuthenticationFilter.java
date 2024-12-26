package com.piramal.sukrtya.security;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.info("Processing request for URI: {}, Authorization Header: {}", request.getRequestURI(), authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            logger.debug("Extracted JWT Token: {}", jwtToken);

            try {
                String username = jwtUtil.extractUsername(jwtToken);
                logger.info("Extracted Username from JWT: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                        var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("JWT Token validated, authentication set for user: {}", username);
                    } else {
                        logger.warn("JWT Token validation failed for user: {}", username);
                    }
                }
            } catch (Exception e) {
                logger.error("Error during JWT authentication process: {}", e.getMessage(), e);

                // Create an ApiResponse object for error handling
                ApiResponse<Object> apiResponse = new ApiResponse<>(
                        "error",
                        "Invalid or expired token",
                        null
                );

                // Write the error response in proper JSON format
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), apiResponse);

                logger.info("Unauthorized response sent for URI: {}", request.getRequestURI());
                return; // Exit the filter after sending the response
            }
        } else {
            logger.debug("No Bearer token found in Authorization header for URI: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
        logger.debug("Request processing completed for URI: {}", request.getRequestURI());
    }
}