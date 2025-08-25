package org.example.proyectofinal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar una categoría existente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar una categoría existente")
public class CategoriaUpdateDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Electrónicos", required = true)
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Schema(description = "Descripción detallada de la categoría", example = "Productos electrónicos y tecnológicos")
    private String descripcion;

    @Schema(description = "Estado activo de la categoría", example = "true")
    private Boolean activo;
}
