package com.shreyas.jwt;


import com.shreyas.customer.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService customerUserDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, CustomerUserDetailsService customerUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Take the Authorization header from the token which is created at time of POST request to Controller
        String authHeader = request.getHeader("Authorization");

        // If authHeader is null, or it does not start with Bearer then go to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Otherwise extract the jwt token from header
        // ( jwt token start from 7th index because "Bearer " is at starting position)
        String jwt = authHeader.substring(7);

        // Get subject from jwt token
        String subject = jwtUtil.getSubject(jwt);

        // If subject is not null and user is not already authenticated(Not present in SecurityContextHolder)
        // we want to authenticate it.
        if (subject != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loads the userDetails (Subject i.e. username is email in this case)
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(subject);

            // We check if the username and subject is matching as well as token is expired or not
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                // If token is valid then we create UsernamePasswordAuthenticationToken by passing the credentials and authorities
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // Set more information about the request on UsernamePasswordAuthenticationToken
                authenticationToken
                        .setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                // Save the UsernamePasswordAuthenticationToken to SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Move to next filter
        filterChain.doFilter(request, response);
    }
}

