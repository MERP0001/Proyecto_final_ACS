package org.example.proyectofinal.specification;

import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.filter.ProductoFilter;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecification {
    public static Specification<Producto> conFiltros(ProductoFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getNombre() != null && !filter.getNombre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filter.getNombre().toLowerCase() + "%"));
            }
            if (filter.getCategoria() != null && !filter.getCategoria().isBlank()) {
                // Buscar en ambos: nueva relación categoria.nombre Y categoriaLegacy
                Predicate categoriaNueva = cb.equal(
                    cb.lower(root.get("categoria").get("nombre")), 
                    filter.getCategoria().toLowerCase()
                );
                Predicate categoriaLegacy = cb.equal(
                    cb.lower(root.get("categoriaLegacy")), 
                    filter.getCategoria().toLowerCase()
                );
                predicates.add(cb.or(categoriaNueva, categoriaLegacy));
            }
            if (filter.getPrecioMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), filter.getPrecioMin()));
            }
            if (filter.getPrecioMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), filter.getPrecioMax()));
            }
            predicates.add(cb.isTrue(root.get("activo")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 