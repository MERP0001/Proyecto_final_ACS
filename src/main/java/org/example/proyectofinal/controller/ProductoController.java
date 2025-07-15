package org.example.proyectofinal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.dto.ErrorResponse;
import org.example.proyectofinal.filter.ProductoFilter;
import org.example.proyectofinal.response.ApiResponse;
import org.example.proyectofinal.service.ProductoService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Productos", description = "Operaciones para la gestión de productos en el inventario.")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en la base de datos.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Creando producto: {}", productoDTO.getNombre());
        ProductoDTO productoCreado = productoService.crearProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID", description = "Recupera los detalles de un producto específico mediante su ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDTO> obtenerProducto(
            @Parameter(description = "ID del producto a obtener", required = true) @PathVariable Long id) {
        log.debug("Obteniendo producto ID: {}", id);
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping
    @Operation(summary = "Listar productos paginados", description = "Lista todos los productos activos con paginación.")
    public ResponseEntity<ApiResponse<Page<ProductoDTO>>> listarProductos(@ParameterObject Pageable pageable) {
        log.debug("Listando productos");
        Page<ProductoDTO> productos = productoService.listarProductosActivos(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos listados", productos));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por filtro", description = "Busca productos aplicando un conjunto de filtros y paginación.")
    public ResponseEntity<ApiResponse<Page<ProductoDTO>>> buscarProductos(
            @ParameterObject ProductoFilter filter,
            @ParameterObject Pageable pageable) {
        log.debug("Buscando productos");
        Page<ProductoDTO> productos = productoService.buscarProductos(filter, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos encontrados", productos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza todos los datos de un producto existente.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Actualizando producto ID: {}", id);
        ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar el stock de un producto", description = "Actualiza únicamente la cantidad de stock de un producto.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDTO> actualizarStock(
            @Parameter(description = "ID del producto para actualizar stock", required = true) @PathVariable Long id,
            @Parameter(description = "Nueva cantidad de stock", required = true) @RequestParam Integer cantidad) {
        log.info("Actualizando stock del producto ID: {}", id);
        ProductoDTO productoActualizado = productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto de la base de datos (borrado lógico).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", required = true) @PathVariable Long id) {
        log.info("Eliminando producto ID: {}", id);
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Obtener productos con stock bajo", description = "Lista los productos cuyo stock es inferior a un umbral mínimo.")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosConStockBajo(
            @Parameter(description = "Cantidad mínima de stock para el filtro", required = true) @RequestParam Integer minimo) {
        log.debug("Obteniendo productos con stock bajo");
        List<ProductoDTO> productos = productoService.obtenerProductosConStockBajo(minimo);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Obtener todas las categorías", description = "Devuelve una lista con todas las categorías de productos existentes.")
    public ResponseEntity<List<String>> obtenerCategorias() {
        log.debug("Obteniendo categorías");
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/valor-total")
    @Operation(summary = "Calcular el valor total del inventario", description = "Calcula el valor total del inventario sumando el precio por la cantidad de cada producto.")
    public ResponseEntity<BigDecimal> calcularValorTotalInventario() {
        log.debug("Calculando valor total del inventario");
        BigDecimal valorTotal = productoService.calcularValorTotalInventario();
        return ResponseEntity.ok(valorTotal);
    }
} 