package org.example.proyectofinal.validator;

import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;

@Component
public class ProductoValidator {
    public void validar(Producto producto) {
        if (producto == null) {
            throw new BusinessValidationException("Los datos del producto no pueden ser nulos");
        }
        if (!StringUtils.hasText(producto.getNombre())) {
            throw new BusinessValidationException("El nombre del producto es obligatorio");
        }
        
        // Validar categoría: debe tener al menos una categoría (nueva relación o legacy)
        if (!producto.tieneCategoria()) {
            throw new BusinessValidationException("La categoría del producto es obligatoria");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("El precio debe ser mayor que 0");
        }
        if (producto.getCantidadInicial() == null || producto.getCantidadInicial() < 0) {
            throw new BusinessValidationException("La cantidad inicial no puede ser negativa");
        }
    }
} 