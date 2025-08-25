package org.example.proyectofinal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de categorías.
 * Utilizado en las operaciones de la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de una categoría en el sistema de inventarios")
public class CategoriaDTO {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Electrónicos", required = true)
    private String nombre;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Schema(description = "Descripción de la categoría", 
            example = "Productos electrónicos y tecnológicos")
    private String descripcion;

    @Schema(description = "Estado de la categoría", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creación de la categoría")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última modificación")
    private LocalDateTime fechaModificacion;

    @Schema(description = "Versión para control de concurrencia optimista")
    private Long version;

    @Schema(description = "Cantidad de productos activos en esta categoría", example = "25")
    private Long cantidadProductos;
}
