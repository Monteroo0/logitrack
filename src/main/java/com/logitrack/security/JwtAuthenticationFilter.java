package com.logitrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        try { System.out.println("JWT Filter: " + request.getMethod() + " " + request.getRequestURI()); } catch (Exception ignored) {}
        try { System.out.println("JWT Auth header: " + (authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 80)) : "<none>")); } catch (Exception ignored) {}

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
                try { System.out.println("JWT User extracted: " + username); } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }

        org.springframework.security.core.Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (username != null && existing == null) {
            String role = null;
            try { role = jwtUtil.extractClaim(token, "role"); } catch (Exception ignored) {}
            java.util.Collection<org.springframework.security.core.GrantedAuthority> authorities = role != null ? java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.toUpperCase())) : java.util.List.of();
            org.springframework.security.core.userdetails.UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
                boolean valid = false;
                try { valid = jwtUtil.isTokenValid(token, username); } catch (Exception ignored) {}
                try { System.out.println("JWT Valid: " + valid + ", role claim: " + role); } catch (Exception ignored) {}
                if (valid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    try { System.out.println("JWT Auth set for: " + username); } catch (Exception ignored) {}
                }
            } catch (Exception ex) {
                boolean valid = false;
                try { valid = jwtUtil.isTokenValid(token, username); } catch (Exception ignored) {}
                try { System.out.println("JWT Fallback Valid: " + valid + ", role claim: " + role); } catch (Exception ignored) {}
                if (valid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    try { System.out.println("JWT Auth set (fallback) for: " + username); } catch (Exception ignored) {}
                }
            }
        }
        try { System.out.println("JWT Filter end"); } catch (Exception ignored) {}
        filterChain.doFilter(request, response);
    }
}