package org.example.proyectofinal.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.ErrorResponse;
import org.example.proyectofinal.dto.LoginRequest;
import org.example.proyectofinal.dto.LoginResponse;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.config.JwtService;
import org.example.proyectofinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

   @Autowired
   private AuthenticationManager authenticationManager;

   @Autowired
   private UserService userService;

   @Autowired
   private PasswordEncoder passwordEncoder;

   @Autowired
   private JwtService jwtService;

   @GetMapping("/test")
   public ResponseEntity<String> test() {
      log.info("=== AuthController TEST llamado ===");
      return ResponseEntity.ok("AuthController funciona correctamente!");
   }

   @PostMapping("/debug")
   public ResponseEntity<Map<String, Object>> debug(@RequestBody LoginRequest loginRequest) {
      Map<String, Object> response = new HashMap<>();

      try {
         log.info("=== DEBUG LOGIN: {} ===", loginRequest.getUsername());

         // 1. Verificar si el usuario existe
         UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
         response.put("userFound", true);
         response.put("username", userDetails.getUsername());
         response.put("authorities", userDetails.getAuthorities());
         response.put("enabled", userDetails.isEnabled());
         response.put("accountNonLocked", userDetails.isAccountNonLocked());

         // 2. Verificar la contraseña manualmente
         boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
         response.put("passwordMatches", passwordMatches);
         response.put("storedPasswordPrefix",
               userDetails.getPassword().substring(0, Math.min(10, userDetails.getPassword().length())));

         // 3. Intentar autenticación
         try {
            Authentication authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
            response.put("authenticationSuccess", true);
            response.put("principal", authentication.getPrincipal().getClass().getSimpleName());
         } catch (Exception authEx) {
            response.put("authenticationSuccess", false);
            response.put("authenticationError", authEx.getMessage());
         }

      } catch (Exception e) {
         response.put("userFound", false);
         response.put("error", e.getMessage());
         log.error("=== DEBUG ERROR: {} ===", e.getMessage());
      }

      return ResponseEntity.ok(response);
   }

   @PostMapping("/generate-hash")
   public ResponseEntity<Map<String, String>> generateHash(@RequestBody Map<String, String> request) {
      String password = request.get("password");
      String hash = passwordEncoder.encode(password);

      Map<String, String> response = new HashMap<>();
      response.put("password", password);
      response.put("hash", hash);
      response.put("matches", String.valueOf(passwordEncoder.matches(password, hash)));

      return ResponseEntity.ok(response);
   }

   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
      try {
         log.info("=== LOGIN ATTEMPT: {} ===", loginRequest.getUsername());

         // Autenticar usuario
         Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                     loginRequest.getUsername(),
                     loginRequest.getPassword()));

         // Obtener detalles del usuario autenticado
         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
         String token = jwtService.generateToken(userDetails);
         User user = (User) userService.loadUserByUsername(loginRequest.getUsername());

         // Actualizar último acceso
         userService.updateLastAccess(user.getId());

         log.info("=== AUTENTICACIÓN EXITOSA PARA: {} ===", user.getUsername());

         return ResponseEntity.ok(LoginResponse.builder()
               .token(token)
               .username(user.getUsername())
               .email(user.getEmail())
               .nombreCompleto(user.getNombreCompleto())
               .role(user.getRole().name())
               .expiresAt(jwtService.getExpirationDateTime())
               .build());

      } catch (BadCredentialsException e) {
         log.error("=== ERROR DE CREDENCIALES: {} ===", e.getMessage());
         return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body(ErrorResponse.builder()
                     .message("Credenciales inválidas")
                     .status(HttpStatus.UNAUTHORIZED.value())
                     .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                     .build());
      } catch (DisabledException e) {
         log.error("=== CUENTA DESACTIVADA: {} ===", e.getMessage());
         return ResponseEntity
               .status(HttpStatus.FORBIDDEN)
               .body(ErrorResponse.builder()
                     .message("Cuenta desactivada")
                     .status(HttpStatus.FORBIDDEN.value())
                     .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                     .build());
      } catch (UsernameNotFoundException e) {
         log.error("=== USUARIO NO ENCONTRADO: {} ===", e.getMessage());
         return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body(ErrorResponse.builder()
                     .message("Usuario no encontrado")
                     .status(HttpStatus.UNAUTHORIZED.value())
                     .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                     .build());
      } catch (Exception e) {
         log.error("=== ERROR EN LOGIN: {} ===", e.getMessage());
         return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(ErrorResponse.builder()
                     .message("Error en el servidor")
                     .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                     .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                     .build());
      }
   }
}