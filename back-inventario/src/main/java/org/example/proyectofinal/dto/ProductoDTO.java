package org.example.proyectofinal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de productos.
 * Utilizado en las operaciones de la API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un producto en el sistema de inventarios")
public class ProductoDTO {

    @Schema(description = "ID único del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Laptop Dell Inspiron 15", required = true)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción detallada del producto", 
            example = "Laptop Dell Inspiron 15 con procesador Intel Core i5, 8GB RAM, 256GB SSD")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 2, max = 50, message = "La categoría debe tener entre 2 y 50 caracteres")
    @Schema(description = "Categoría del producto", example = "Electrónicos", required = true)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Schema(description = "Precio del producto", example = "750.50", required = true)
    private BigDecimal precio;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Schema(description = "Cantidad inicial del producto en inventario", example = "10", required = true)
    private Integer cantidadInicial;

    @Min(value = 0, message = "La cantidad actual no puede ser negativa")
    @Schema(description = "Cantidad actual disponible en inventario", example = "8", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer cantidadActual;

    @Schema(description = "Estado activo del producto", example = "true")
    @Builder.Default
    private Boolean activo = true;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Schema(description = "Unidad de medida del producto", example = "UNIDAD")
    @Builder.Default
    private String unidadMedida = "UNIDAD";

    @Size(max = 100, message = "El código SKU no puede exceder 100 caracteres")
    @Schema(description = "Código SKU único del producto", example = "DELL-INSP-15-001")
    private String sku;

    @Schema(description = "Fecha de creación del producto", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última modificación", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaModificacion;

    @Schema(description = "Versión para control de concurrencia optimista", accessMode = Schema.AccessMode.READ_ONLY)
    private Long version;

    // Campos calculados para la API
    @Schema(description = "Indica si el producto tiene stock disponible", accessMode = Schema.AccessMode.READ_ONLY)
    public boolean getTieneStock() {
        return cantidadActual != null && cantidadActual > 0;
    }

    @Schema(description = "Valor total del inventario de este producto", accessMode = Schema.AccessMode.READ_ONLY)
    public BigDecimal getValorInventario() {
        if (precio != null && cantidadActual != null) {
            return precio.multiply(BigDecimal.valueOf(cantidadActual));
        }
        return BigDecimal.ZERO;
    }
} 