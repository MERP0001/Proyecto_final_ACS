package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario por su ID o criterio de búsqueda.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Long id) {
        super("No se encontró el usuario con ID: " + id);
    }

    public static UserNotFoundException porUsername(String username) {
        return new UserNotFoundException("No se encontró el usuario con username: " + username);
    }
}