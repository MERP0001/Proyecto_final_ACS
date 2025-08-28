package org.example.proyectofinal.exception;

import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para las excepciones relacionadas con Categoria.
 */
class CategoriaExceptionTest {

    @Test
    void testCategoriaNotFoundException() {
        // Arrange
        Long categoriaId = 123L;

        // Act
        CategoriaNotFoundException exception = new CategoriaNotFoundException(categoriaId);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(categoriaId.toString());
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCategoriaNotFoundExceptionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "No se encontró la categoría especificada";

        // Act
        CategoriaNotFoundException exception = new CategoriaNotFoundException(mensajePersonalizado);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensajePersonalizado);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCategoriaAlreadyExistsException() {
        // Arrange
        String nombreCategoria = "Electrónicos";

        // Act
        CategoriaAlreadyExistsException exception = new CategoriaAlreadyExistsException(nombreCategoria);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(nombreCategoria);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCategoriaAlreadyExistsExceptionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "La categoría ya existe en el sistema";

        // Act
        CategoriaAlreadyExistsException exception = new CategoriaAlreadyExistsException(mensajePersonalizado);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensajePersonalizado);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testBusinessValidationExceptionParaCategoria() {
        // Arrange
        String campo = "nombre";
        String valor = "";
        String razon = "El nombre de la categoría no puede estar vacío";

        // Act
        BusinessValidationException exception = new BusinessValidationException(campo, valor, razon);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains(campo);
        assertThat(exception.getMessage()).contains(razon);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testBusinessValidationExceptionConMensajeSimple() {
        // Arrange
        String mensaje = "Error de validación en categoría";

        // Act
        BusinessValidationException exception = new BusinessValidationException(mensaje);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testJerarquiaDeExcepciones() {
        // Arrange & Act
        CategoriaNotFoundException notFoundEx = new CategoriaNotFoundException(1L);
        CategoriaAlreadyExistsException alreadyExistsEx = new CategoriaAlreadyExistsException("test");
        BusinessValidationException validationEx = new BusinessValidationException("test");

        // Assert - Todas deben ser RuntimeException
        assertThat(notFoundEx).isInstanceOf(RuntimeException.class);
        assertThat(alreadyExistsEx).isInstanceOf(RuntimeException.class);
        assertThat(validationEx).isInstanceOf(RuntimeException.class);

        // Assert - Cada excepción debe tener su tipo específico
        assertThat(notFoundEx).isInstanceOf(CategoriaNotFoundException.class);
        assertThat(alreadyExistsEx).isInstanceOf(CategoriaAlreadyExistsException.class);
        assertThat(validationEx).isInstanceOf(BusinessValidationException.class);
    }

    @Test
    void testExcepcionesConDiferentesTiposDeParametros() {
        // Act & Assert - ID nulo
        CategoriaNotFoundException exceptionIdNulo = new CategoriaNotFoundException((Long) null);
        assertThat(exceptionIdNulo.getMessage()).contains("null");

        // Act & Assert - ID cero
        CategoriaNotFoundException exceptionIdCero = new CategoriaNotFoundException(0L);
        assertThat(exceptionIdCero.getMessage()).contains("0");

        // Act & Assert - ID negativo
        CategoriaNotFoundException exceptionIdNegativo = new CategoriaNotFoundException(-1L);
        assertThat(exceptionIdNegativo.getMessage()).contains("-1");

        // Act & Assert - Nombre nulo
        CategoriaAlreadyExistsException exceptionNombreNulo = new CategoriaAlreadyExistsException((String) null);
        assertThat(exceptionNombreNulo.getMessage()).contains("null");

        // Act & Assert - Nombre vacío
        CategoriaAlreadyExistsException exceptionNombreVacio = new CategoriaAlreadyExistsException("");
        assertThat(exceptionNombreVacio.getMessage()).isNotNull();
    }

    @Test
    void testMensajesDeExcepcionConFormato() {
        // Arrange
        Long id = 999L;
        String nombre = "Categoría de Prueba";

        // Act
        CategoriaNotFoundException notFoundEx = new CategoriaNotFoundException(id);
        CategoriaAlreadyExistsException alreadyExistsEx = new CategoriaAlreadyExistsException(nombre);

        // Assert - Verificar formato específico de mensajes
        assertThat(notFoundEx.getMessage()).contains(id.toString());
        assertThat(alreadyExistsEx.getMessage()).contains(nombre);
    }

    @Test
    void testExcepcionesParaValidacionesEspecificasDeCategoria() {
        // Pruebas para validaciones específicas que podrían usarse en CategoriaService

        // Validación de nombre duplicado
        BusinessValidationException nombreDuplicado = new BusinessValidationException(
                "nombre", "Electrónicos", "Ya existe una categoría con este nombre"
        );
        assertThat(nombreDuplicado.getMessage()).contains("nombre");
        assertThat(nombreDuplicado.getMessage()).contains("Electrónicos");

        // Validación de nombre muy largo
        BusinessValidationException nombreLargo = new BusinessValidationException(
                "nombre", "A".repeat(300), "El nombre no puede exceder 255 caracteres"
        );
        assertThat(nombreLargo.getMessage()).contains("255 caracteres");

        // Validación de descripción muy larga
        BusinessValidationException descripcionLarga = new BusinessValidationException(
                "descripcion", "B".repeat(1500), "La descripción no puede exceder 1000 caracteres"
        );
        assertThat(descripcionLarga.getMessage()).contains("descripcion");

        // Validación de categoría inactiva
        BusinessValidationException categoriaInactiva = new BusinessValidationException(
                "activo", "false", "No se puede operar con una categoría inactiva"
        );
        assertThat(categoriaInactiva.getMessage()).contains("activo");
    }
}
