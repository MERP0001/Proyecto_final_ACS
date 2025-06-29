package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando una operación viola las reglas de negocio.
 * Por ejemplo: stock insuficiente, producto inactivo, etc.
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 