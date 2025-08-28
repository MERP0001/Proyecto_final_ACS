package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando se intenta crear un usuario que ya existe.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserAlreadyExistsException porUsername(String username) {
        return new UserAlreadyExistsException("Ya existe un usuario con el username: " + username);
    }

    public static UserAlreadyExistsException porEmail(String email) {
        return new UserAlreadyExistsException("Ya existe un usuario con el email: " + email);
    }
}