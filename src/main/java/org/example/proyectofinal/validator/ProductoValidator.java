package org.example.proyectofinal.validator;

import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;

@Component
public class ProductoValidator {
    public void validar(ProductoDTO productoDTO) {
        if (productoDTO == null) {
            throw new BusinessValidationException("Los datos del producto no pueden ser nulos");
        }
        if (!StringUtils.hasText(productoDTO.getNombre())) {
            throw new BusinessValidationException("El nombre del producto es obligatorio");
        }
        if (!StringUtils.hasText(productoDTO.getCategoria())) {
            throw new BusinessValidationException("La categoría del producto es obligatoria");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("El precio debe ser mayor que 0");
        }
        if (productoDTO.getCantidadInicial() == null || productoDTO.getCantidadInicial() < 0) {
            throw new BusinessValidationException("La cantidad inicial no puede ser negativa");
        }
    }
} 