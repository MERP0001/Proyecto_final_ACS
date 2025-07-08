package org.example.proyectofinal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.config.JwtService;
import org.example.proyectofinal.dto.AuthRequest;
import org.example.proyectofinal.dto.AuthResponse;
import org.example.proyectofinal.dto.RefreshTokenRequest;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

   private final AuthenticationManager authenticationManager;
   private final UserService userService;
   private final JwtService jwtService;

   @PostMapping("/login")
   public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
      try {
         log.debug("Intento de login para usuario: {}", request.getUsername());

         Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                     request.getUsername(),
                     request.getPassword()));

         User user = (User) authentication.getPrincipal();
         String accessToken = jwtService.generateAccessToken(user);
         String refreshToken = jwtService.generateRefreshToken(user);

         Instant expiresAt = Instant.now().plus(jwtService.getAccessTokenExpiration(), ChronoUnit.MILLIS);

         userService.updateLastAccess(user.getId());

         log.info("Login exitoso para usuario: {}", user.getUsername());

         return ResponseEntity.ok(AuthResponse.success(
               accessToken,
               refreshToken,
               expiresAt,
               user.getUsername(),
               user.getEmail(),
               user.getNombreCompleto(),
               user.getRole().name()));

      } catch (BadCredentialsException e) {
         log.warn("Credenciales inválidas para usuario: {}", request.getUsername());
         return ResponseEntity.badRequest().body(AuthResponse.error("Credenciales inválidas"));
      } catch (DisabledException e) {
         log.warn("Cuenta deshabilitada para usuario: {}", request.getUsername());
         return ResponseEntity.badRequest().body(AuthResponse.error("Cuenta deshabilitada"));
      } catch (Exception e) {
         log.error("Error en login para usuario: {}", request.getUsername(), e);
         return ResponseEntity.internalServerError().body(AuthResponse.error("Error en el servidor"));
      }
   }

   @PostMapping("/refresh-token")
   public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
      try {
         String username = jwtService.extractUsername(request.getRefreshToken());
         User user = (User) userService.loadUserByUsername(username);

         if (jwtService.isTokenValid(request.getRefreshToken(), user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            Instant expiresAt = Instant.now().plus(jwtService.getAccessTokenExpiration(), ChronoUnit.MILLIS);

            return ResponseEntity.ok(AuthResponse.success(
                  accessToken,
                  refreshToken,
                  expiresAt,
                  user.getUsername(),
                  user.getEmail(),
                  user.getNombreCompleto(),
                  user.getRole().name()));
         }

         return ResponseEntity.badRequest().body(AuthResponse.error("Token de refresco inválido"));
      } catch (Exception e) {
         log.error("Error al refrescar token", e);
         return ResponseEntity.internalServerError().body(AuthResponse.error("Error al refrescar token"));
      }
   }

   @PostMapping("/validate-token")
   public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
      try {
         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(AuthResponse.error("Token no proporcionado"));
         }

         String token = authHeader.substring(7);
         String username = jwtService.extractUsername(token);
         User user = (User) userService.loadUserByUsername(username);

         if (jwtService.isTokenValid(token, user)) {
            return ResponseEntity.ok(AuthResponse.success(
                  null,
                  null,
                  null,
                  user.getUsername(),
                  user.getEmail(),
                  user.getNombreCompleto(),
                  user.getRole().name()));
         }

         return ResponseEntity.badRequest().body(AuthResponse.error("Token inválido"));
      } catch (Exception e) {
         log.error("Error al validar token", e);
         return ResponseEntity.internalServerError().body(AuthResponse.error("Error al validar token"));
      }
   }

   @PostMapping("/logout")
   public ResponseEntity<AuthResponse> logout() {
      SecurityContextHolder.clearContext();
      return ResponseEntity.ok(AuthResponse.builder()
            .message("Sesión cerrada exitosamente")
            .build());
   }
}