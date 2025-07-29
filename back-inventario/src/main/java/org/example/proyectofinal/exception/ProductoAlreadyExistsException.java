package org.example.proyectofinal.exception;

/**
 * Excepción lanzada cuando se intenta crear un producto que ya existe.
 * Comúnmente usado para SKU duplicados.
 */
public class ProductoAlreadyExistsException extends RuntimeException {

    public ProductoAlreadyExistsException(String message) {
        super(message);
    }

    public ProductoAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ProductoAlreadyExistsException porSku(String sku) {
        return new ProductoAlreadyExistsException("Ya existe un producto con SKU: " + sku);
    }

    public static ProductoAlreadyExistsException porCampo(String campo, String valor) {
        return new ProductoAlreadyExistsException("Ya existe un producto con " + campo + ": " + valor);
    }
} 