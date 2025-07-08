package org.example.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
   private String accessToken;
   private String refreshToken;
   private String tokenType;
   private Instant expiresAt;
   private String username;
   private String email;
   private String nombreCompleto;
   private String role;
   private String message;

   public static AuthResponse error(String message) {
      return AuthResponse.builder()
            .message(message)
            .build();
   }

   public static AuthResponse success(String accessToken, String refreshToken, Instant expiresAt,
         String username, String email, String nombreCompleto, String role) {
      return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresAt(expiresAt)
            .username(username)
            .email(email)
            .nombreCompleto(nombreCompleto)
            .role(role)
            .build();
   }
}