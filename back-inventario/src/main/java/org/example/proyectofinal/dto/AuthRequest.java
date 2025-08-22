package org.example.proyectofinal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

   @NotBlank(message = "El nombre de usuario es requerido")
   @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
   private String username;

   @NotBlank(message = "La contraseña es requerida")
   @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
   private String password;
}