package org.example.proyectofinal.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.LoginRequest;
import org.example.proyectofinal.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("=== AuthController TEST llamado ===");
        return ResponseEntity.ok("AuthController funciona correctamente!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("=== LOGIN ATTEMPT: {} ===", loginRequest.getUsername());
            
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            
            log.info("=== AUTENTICACIÓN EXITOSA ===");
            
            // Por ahora, devolver token dummy
            return ResponseEntity.ok(LoginResponse.builder()
                .token("dummy-jwt-token")
                .username(loginRequest.getUsername())
                .build());
                
        } catch (Exception e) {
            log.error("=== ERROR EN LOGIN: {} ===", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }
} 