package org.example.proyectofinal.validator;

import lombok.RequiredArgsConstructor;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Validador de reglas de negocio para entidades Categoria.
 * Centraliza las validaciones complejas que van más allá de Bean Validation.
 */
@Component
@RequiredArgsConstructor
public class CategoriaValidator {

    /**
     * Valida una categoría antes de crear o actualizar.
     *
     * @param categoria categoría a validar
     * @throws BusinessValidationException si la validación falla
     */
    public void validar(Categoria categoria) {
        if (categoria == null) {
            throw new BusinessValidationException("Los datos de la categoría no pueden ser nulos");
        }

        validarNombre(categoria.getNombre());
        validarDescripcion(categoria.getDescripcion());
        validarEstado(categoria.getActivo());
    }

    /**
     * Valida el nombre de la categoría.
     *
     * @param nombre nombre a validar
     * @throws BusinessValidationException si el nombre no es válido
     */
    private void validarNombre(String nombre) {
        if (!StringUtils.hasText(nombre)) {
            throw new BusinessValidationException("El nombre de la categoría es obligatorio");
        }

        if (nombre.trim().length() < 2) {
            throw new BusinessValidationException("El nombre de la categoría debe tener al menos 2 caracteres");
        }

        if (nombre.length() > 50) {
            throw new BusinessValidationException("El nombre de la categoría no puede exceder 50 caracteres");
        }

        // Validar que no contenga solo espacios
        if (nombre.trim().isEmpty()) {
            throw new BusinessValidationException("El nombre de la categoría no puede contener solo espacios");
        }

        // Validar caracteres permitidos (letras, números, espacios, algunos caracteres especiales)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s\\-&/()]+$")) {
            throw new BusinessValidationException("El nombre de la categoría contiene caracteres no permitidos");
        }
    }

    /**
     * Valida la descripción de la categoría.
     *
     * @param descripcion descripción a validar
     * @throws BusinessValidationException si la descripción no es válida
     */
    private void validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 200) {
            throw new BusinessValidationException("La descripción no puede exceder 200 caracteres");
        }

        // Si hay descripción, validar que no sea solo espacios
        if (descripcion != null && !descripcion.trim().isEmpty() && descripcion.trim().length() < 3) {
            throw new BusinessValidationException("La descripción debe tener al menos 3 caracteres");
        }
    }

    /**
     * Valida el estado de la categoría.
     *
     * @param activo estado a validar
     * @throws BusinessValidationException si el estado no es válido
     */
    private void validarEstado(Boolean activo) {
        if (activo == null) {
            throw new BusinessValidationException("El estado de la categoría no puede ser nulo");
        }
    }

    /**
     * Valida que una categoría puede ser eliminada.
     * Una categoría no puede ser eliminada si tiene productos activos asociados.
     *
     * @param categoria categoría a validar para eliminación
     * @param tieneProductosActivos indica si la categoría tiene productos activos
     * @throws BusinessValidationException si la categoría no puede ser eliminada
     */
    public void validarEliminacion(Categoria categoria, boolean tieneProductosActivos) {
        if (categoria == null) {
            throw new BusinessValidationException("La categoría a eliminar no puede ser nula");
        }

        if (tieneProductosActivos) {
            throw new BusinessValidationException(
                "No se puede eliminar la categoría '" + categoria.getNombre() + 
                "' porque tiene productos activos asociados"
            );
        }
    }
}
