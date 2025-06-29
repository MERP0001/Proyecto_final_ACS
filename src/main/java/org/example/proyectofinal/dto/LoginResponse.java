package org.example.proyectofinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private String email;
    private String nombreCompleto;
    private String role;
    private LocalDateTime expiresAt;
} 