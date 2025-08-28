package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando no se encuentra un producto por su ID o criterio de búsqueda.
 */
public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String message) {
        super(message);
    }

    public ProductoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }

    public ProductoNotFoundException(String campo, String valor) {
        super("Producto no encontrado con " + campo + ": " + valor);
    }

    public static ProductoNotFoundException porSku(String sku) {
        return new ProductoNotFoundException("No se encontró el producto con SKU: " + sku);
    }
} 