package org.example.proyectofinal.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filtro para búsqueda de categorías.
 * Permite filtrar categorías por múltiples criterios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaFilter {
    
    /**
     * Filtro por nombre de categoría (búsqueda parcial, case-insensitive).
     */
    private String nombre;
    
    /**
     * Filtro por descripción de categoría (búsqueda parcial, case-insensitive).
     */
    private String descripcion;
    
    /**
     * Filtro por estado activo/inactivo.
     * Si es null, incluye todas las categorías.
     */
    private Boolean activo;
}
