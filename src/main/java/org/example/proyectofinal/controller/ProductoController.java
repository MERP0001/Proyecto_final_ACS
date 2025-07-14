package org.example.proyectofinal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.filter.ProductoFilter;
import org.example.proyectofinal.response.ApiResponse;
import org.example.proyectofinal.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 */
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Creando producto: {}", productoDTO.getNombre());
        ProductoDTO productoCreado = productoService.crearProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        log.debug("Obteniendo producto ID: {}", id);
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductoDTO>>> listarProductos(Pageable pageable) {
        log.debug("Listando productos");
        Page<ProductoDTO> productos = productoService.listarProductosActivos(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos listados", productos));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<ProductoDTO>>> buscarProductos(
            ProductoFilter filter,
            Pageable pageable) {
        
        log.debug("Buscando productos");
        Page<ProductoDTO> productos = productoService.buscarProductos(filter, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos encontrados", productos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Actualizando producto ID: {}", id);
        ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoDTO> actualizarStock(
            @PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("Actualizando stock del producto ID: {}", id);
        ProductoDTO productoActualizado = productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("Eliminando producto ID: {}", id);
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosConStockBajo(@RequestParam Integer minimo) {
        log.debug("Obteniendo productos con stock bajo");
        List<ProductoDTO> productos = productoService.obtenerProductosConStockBajo(minimo);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        log.debug("Obteniendo categorías");
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/valor-total")
    public ResponseEntity<BigDecimal> calcularValorTotalInventario() {
        log.debug("Calculando valor total del inventario");
        BigDecimal valorTotal = productoService.calcularValorTotalInventario();
        return ResponseEntity.ok(valorTotal);
    }
} 