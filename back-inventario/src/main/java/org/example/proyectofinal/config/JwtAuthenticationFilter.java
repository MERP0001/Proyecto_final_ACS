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

      final String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         filterChain.doFilter(request, response);
         return;
      }

      try {
         final String jwt = authHeader.substring(7);
         final String username = jwtService.extractUsername(jwt);

         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails,
                     null,
                     userDetails.getAuthorities());
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);
               log.debug("User '{}' authenticated successfully for path: {}", username, request.getServletPath());
            }
         }
         
         filterChain.doFilter(request, response);

      } catch (ExpiredJwtException e) {
         log.warn("JWT Token has expired for path: {}", request.getServletPath());
         sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
      } catch (JwtException e) {
         log.error("Invalid JWT Token for path: {}. Reason: {}", request.getServletPath(), e.getMessage());
         sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
      } catch (Exception e) {
         log.error("Authentication error for path: {}", request.getServletPath(), e);
         sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de autenticación");
      }
   }
   
   private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
      response.setStatus(status);
      response.setContentType("application/json");
      response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
   }
}