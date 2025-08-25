package org.example.proyectofinal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el sistema de gestión de inventarios.
 * Incluye auditoría automática y validaciones.
 */
@Entity
@Table(name = "productos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    // MIGRACIÓN EN PROGRESO: Mantenemos ambas columnas durante la transición
    
    /**
     * Relación con la entidad Categoria.
     * Esta es la nueva forma de gestionar categorías.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", foreignKey = @ForeignKey(name = "fk_productos_categoria"))
    private Categoria categoria;

    /**
     * Campo legacy de categoría como String.
     * @deprecated Usar la relación {@link #categoria} en su lugar.
     * Este campo se mantendrá temporalmente para la migración.
     */
    @Deprecated
    @Column(name = "categoria", length = 50)
    private String categoriaLegacy;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Min(value = 0, message = "La cantidad actual no puede ser negativa")
    @Column(name = "cantidad_actual", nullable = false)
    @Builder.Default
    private Integer cantidadActual = 0;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Column(name = "unidad_medida", length = 50)
    @Builder.Default
    private String unidadMedida = "UNIDAD";

    @Size(max = 100, message = "El código SKU no puede exceder 100 caracteres")
    @Column(name = "sku", unique = true, length = 100)
    private String sku;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Método que se ejecuta antes de persistir la entidad.
     * Inicializa la cantidad actual con la cantidad inicial si no está definida.
     */
    @PrePersist
    protected void onCreate() {
        if (cantidadActual == null) {
            cantidadActual = cantidadInicial;
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    /**
     * Método que se ejecuta antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    /**
     * Método para verificar si el producto tiene stock disponible.
     * @return true si la cantidad actual es mayor que 0
     */
    public boolean tieneStock() {
        return cantidadActual != null && cantidadActual > 0;
    }

    /**
     * Método para verificar si el producto tiene stock suficiente.
     * @param cantidad cantidad requerida
     * @return true si hay stock suficiente
     */
    public boolean tieneStockSuficiente(Integer cantidad) {
        return cantidadActual != null && cantidadActual >= cantidad;
    }

    /**
     * Obtiene el nombre de la categoría, priorizando la nueva relación.
     * @return nombre de la categoría
     */
    public String getNombreCategoria() {
        if (categoria != null) {
            return categoria.getNombre();
        }
        return categoriaLegacy;
    }

    /**
     * Establece la categoría por nombre (método auxiliar para migración).
     * @param nombreCategoria nombre de la categoría
     * @deprecated Usar {@link #setCategoria(Categoria)} en su lugar
     */
    @Deprecated
    public void setCategoriaLegacy(String nombreCategoria) {
        this.categoriaLegacy = nombreCategoria;
    }

    /**
     * Verifica si el producto tiene una categoría asignada.
     * @return true si tiene categoría (nueva relación o legacy)
     */
    public boolean tieneCategoria() {
        return categoria != null || (categoriaLegacy != null && !categoriaLegacy.trim().isEmpty());
    }

    /**
     * Método para generar el toString con información relevante.
     */
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + getNombreCategoria() + '\'' +
                ", precio=" + precio +
                ", cantidadActual=" + cantidadActual +
                ", activo=" + activo +
                '}';
    }
} 