package org.example.proyectofinal.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

   private final JwtService jwtService;
   private final UserService userService;

   @Override
   protected void doFilterInternal(
         @NonNull HttpServletRequest request,
         @NonNull HttpServletResponse response,
         @NonNull FilterChain filterChain) throws ServletException, IOException {

      final String requestURI = request.getRequestURI();

      // Skip authentication for public endpoints
      if (requestURI.contains("/auth")) {
         log.trace("Skipping authentication for public endpoint: {}", requestURI);
         filterChain.doFilter(request, response);
         return;
      }

      final String authHeader = request.getHeader("Authorization");

      // If no Authorization header or not a Bearer token, continue without
      // authentication
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         log.debug("No token found in request to: {}", requestURI);
         filterChain.doFilter(request, response);
         return;
      }

      // Extract token (remove "Bearer " prefix)
      final String jwt = authHeader.substring(7);

      try {
         // Extract username from token
         final String username = jwtService.extractUsername(jwt);

         // If username is null or authentication is already set, continue without
         // further processing
         Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
         if (username == null) {
            log.warn("Could not extract username from token for request to: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
         }

         // If no authentication exists yet and username was found in token
         if (existingAuth == null) {
            // Load user details
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) {
               // Create authentication token
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails,
                     null,
                     userDetails.getAuthorities());
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

               // Set authentication in context
               SecurityContextHolder.getContext().setAuthentication(authToken);
               log.debug("Authenticated user '{}' for request to: {}", username, requestURI);
            } else {
               log.warn("Invalid token for user: {} accessing: {}", username, requestURI);
            }
         }
      } catch (AuthenticationException e) {
         log.error("Authentication exception processing token: {}", e.getMessage());
         SecurityContextHolder.clearContext();
      } catch (Exception e) {
         log.error("Error processing JWT token: {}", e.getMessage());
         SecurityContextHolder.clearContext();
      }

      filterChain.doFilter(request, response);
   }
}