package org.example.proyectofinal.entity;

public enum TipoMovimiento {
    ENTRADA,         // Ingreso inicial o adición de stock.
    SALIDA,          // Venta o retiro de stock.
    AJUSTE_POSITIVO, // Ajuste de inventario que suma stock.
    AJUSTE_NEGATIVO  // Ajuste de inventario que resta stock.
} 