package org.example.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas en la API.
 * Proporciona información consistente sobre errores que ocurren en el sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponse {

    @Schema(description = "Timestamp del error", example = "2024-01-15 10:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;

    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensaje principal del error", example = "Producto no encontrado")
    private String message;

    @Schema(description = "Detalles adicionales del error", example = "Producto no encontrado con ID: 123")
    private String details;

    @Schema(description = "Ruta donde ocurrió el error", example = "/api/productos/123")
    private String path;

    @Schema(description = "Lista de errores de validación (si aplica)")
    private List<ValidationError> validationErrors;

    /**
     * Clase interna para errores de validación específicos.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error de validación específico")
    public static class ValidationError {
        
        @Schema(description = "Campo que falló la validación", example = "nombre")
        private String field;
        
        @Schema(description = "Valor rechazado", example = "")
        private Object rejectedValue;
        
        @Schema(description = "Mensaje de error de validación", example = "El nombre es obligatorio")
        private String message;
    }
} 