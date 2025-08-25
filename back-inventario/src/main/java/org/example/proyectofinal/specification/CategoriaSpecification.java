package org.example.proyectofinal.specification;

import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Especificaciones JPA para filtrado dinámico de categorías.
 * Implementa el patrón Specification para consultas tipo-seguras.
 */
public class CategoriaSpecification {

    /**
     * Crea una especificación basada en los filtros proporcionados.
     *
     * @param filter filtros a aplicar
     * @return especificación JPA
     */
    public static Specification<Categoria> conFiltros(CategoriaFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre (búsqueda parcial, case-insensitive)
            if (filter.getNombre() != null && !filter.getNombre().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("nombre")), 
                    "%" + filter.getNombre().toLowerCase() + "%"
                ));
            }

            // Filtro por descripción (búsqueda parcial, case-insensitive)
            if (filter.getDescripcion() != null && !filter.getDescripcion().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("descripcion")), 
                    "%" + filter.getDescripcion().toLowerCase() + "%"
                ));
            }

            // Filtro por estado activo
            if (filter.getActivo() != null) {
                predicates.add(cb.equal(root.get("activo"), filter.getActivo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Especificación para obtener solo categorías activas.
     *
     * @return especificación para categorías activas
     */
    public static Specification<Categoria> activas() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    /**
     * Especificación para ordenar por nombre.
     *
     * @return especificación con ordenamiento por nombre
     */
    public static Specification<Categoria> ordenadaPorNombre() {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("nombre")));
            return cb.conjunction();
        };
    }
}
