package org.example.proyectofinal.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

   private final JwtService jwtService;
   private final UserService userService;

   private static final List<String> PUBLIC_PATHS = Arrays.asList(
         "/api/auth/login",
         "/api/auth/register",
         "/api/auth/refresh-token",
         "/api/auth/validate-token");

   @Override
   protected void doFilterInternal(
         @NonNull HttpServletRequest request,
         @NonNull HttpServletResponse response,
         @NonNull FilterChain filterChain) throws ServletException, IOException {

      final String path = request.getServletPath();

      if (isPublicPath(path)) {
         filterChain.doFilter(request, response);
         return;
      }

      final String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         log.debug("No token found or invalid token format for path: {}", path);
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.getWriter().write("No authentication token provided");
         return;
      }

      final String jwt = authHeader.substring(7);
      try {
         processToken(jwt, request);
         filterChain.doFilter(request, response);
      } catch (ExpiredJwtException e) {
         log.warn("Token expired for path: {}", path);
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.getWriter().write("Token expired");
      } catch (JwtException e) {
         log.error("Invalid token for path: {}", path);
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.getWriter().write("Invalid token");
      } catch (Exception e) {
         log.error("Authentication error for path: {}", path, e);
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.getWriter().write("Authentication error occurred");
      }
   }

   private void processToken(String token, HttpServletRequest request) {
      final String username = jwtService.extractUsername(token);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
         UserDetails userDetails = userService.loadUserByUsername(username);
         if (jwtService.isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("Authenticated user '{}' for path: {}", username, request.getServletPath());
         }
      }
   }

   private boolean isPublicPath(String path) {
      return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
   }
}