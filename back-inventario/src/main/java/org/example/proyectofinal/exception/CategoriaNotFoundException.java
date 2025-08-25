package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando no se encuentra una categoría solicitada.
 * Típicamente ocurre en operaciones de búsqueda por ID.
 */
public class CategoriaNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Categoría no encontrada";

    public CategoriaNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CategoriaNotFoundException(String message) {
        super(message);
    }

    public CategoriaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoriaNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    /**
     * Crea una excepción específica para ID de categoría no encontrado.
     *
     * @param id ID de la categoría que no se encontró
     * @return excepción configurada
     */
    public static CategoriaNotFoundException porId(Long id) {
        return new CategoriaNotFoundException(
            "No se encontró la categoría con ID: " + id
        );
    }

    /**
     * Crea una excepción específica para nombre de categoría no encontrado.
     *
     * @param nombre nombre de la categoría que no se encontró
     * @return excepción configurada
     */
    public static CategoriaNotFoundException porNombre(String nombre) {
        return new CategoriaNotFoundException(
            "No se encontró la categoría con nombre: " + nombre
        );
    }
}
