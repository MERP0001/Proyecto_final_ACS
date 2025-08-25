package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando se intenta crear una categoría que ya existe.
 * Típicamente ocurre cuando se intenta duplicar el nombre de una categoría.
 */
public class CategoriaAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "La categoría ya existe";

    public CategoriaAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public CategoriaAlreadyExistsException(String message) {
        super(message);
    }

    public CategoriaAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoriaAlreadyExistsException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    /**
     * Crea una excepción específica para nombre de categoría duplicado.
     *
     * @param nombre nombre de la categoría que ya existe
     * @return excepción configurada
     */
    public static CategoriaAlreadyExistsException porNombre(String nombre) {
        return new CategoriaAlreadyExistsException(
            "Ya existe una categoría con el nombre: " + nombre
        );
    }

    /**
     * Crea una excepción específica para ID de categoría duplicado.
     *
     * @param id ID de la categoría que ya existe
     * @return excepción configurada
     */
    public static CategoriaAlreadyExistsException porId(Long id) {
        return new CategoriaAlreadyExistsException(
            "Ya existe una categoría con el ID: " + id
        );
    }
}
