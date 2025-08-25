package org.example.proyectofinal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.CategoriaCreateDTO;
import org.example.proyectofinal.dto.CategoriaDTO;
import org.example.proyectofinal.dto.CategoriaUpdateDTO;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.mapper.CategoriaMapper;
import org.example.proyectofinal.service.CategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de categorías.
 * Proporciona endpoints para operaciones CRUD y consultas especializadas.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorias", description = "API para la gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    /**
     * Crear una nueva categoría.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva categoría", 
               description = "Crea una nueva categoría en el sistema. Solo usuarios administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre"),
        @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<CategoriaDTO> crearCategoria(
            @Parameter(description = "Datos de la categoría a crear", required = true)
            @Valid @RequestBody CategoriaCreateDTO categoriaCreateDTO) {
        
        log.info("POST /api/categorias - Creando categoría: {}", categoriaCreateDTO.getNombre());
        
        Categoria categoria = categoriaMapper.toEntity(categoriaCreateDTO);
        Categoria categoriaCreada = categoriaService.crearCategoria(categoria);
        CategoriaDTO categoriaDTO = categoriaMapper.toDTO(categoriaCreada);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaDTO);
    }

    /**
     * Obtener categoría por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", 
               description = "Recupera una categoría específica por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id) {
        
        log.debug("GET /api/categorias/{} - Obteniendo categoría", id);
        
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        CategoriaDTO categoriaDTO = categoriaMapper.toDTO(categoria);
        
        return ResponseEntity.ok(categoriaDTO);
    }

    /**
     * Listar categorías con paginación y filtros.
     */
    @GetMapping
    @Operation(summary = "Listar categorías", 
               description = "Obtiene una lista paginada de categorías con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    })
    public ResponseEntity<Page<CategoriaDTO>> listarCategorias(
            @Parameter(description = "Nombre de la categoría (búsqueda parcial)")
            @RequestParam(required = false) String nombre,
            
            @Parameter(description = "Estado activo de la categoría")
            @RequestParam(required = false) Boolean activo,
            
            @Parameter(description = "Número de página (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "nombre") String sortBy,
            
            @Parameter(description = "Dirección de ordenamiento (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.debug("GET /api/categorias - Listando categorías con filtros");
        
        // Crear filtro
        CategoriaFilter filter = CategoriaFilter.builder()
                .nombre(nombre)
                .activo(activo)
                .build();
        
        // Crear paginación y ordenamiento
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Buscar categorías
        Page<Categoria> categorias = categoriaService.buscarCategorias(filter, pageable);
        Page<CategoriaDTO> categoriasDTO = categorias.map(categoriaMapper::toDTO);
        
        return ResponseEntity.ok(categoriasDTO);
    }

    /**
     * Obtener todas las categorías activas (sin paginación).
     */
    @GetMapping("/activas")
    @Operation(summary = "Obtener categorías activas", 
               description = "Obtiene todas las categorías activas ordenadas por nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías activas")
    })
    public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasActivas() {
        log.debug("GET /api/categorias/activas - Obteniendo categorías activas");
        
        List<Categoria> categorias = categoriaService.obtenerCategoriasActivas();
        List<CategoriaDTO> categoriasDTO = categoriaMapper.toDTOList(categorias);
        
        return ResponseEntity.ok(categoriasDTO);
    }

    /**
     * Actualizar una categoría existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar categoría", 
               description = "Actualiza una categoría existente. Solo usuarios administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre"),
        @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<CategoriaDTO> actualizarCategoria(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Datos actualizados de la categoría", required = true)
            @Valid @RequestBody CategoriaUpdateDTO categoriaUpdateDTO) {
        
        log.info("PUT /api/categorias/{} - Actualizando categoría", id);
        
        Categoria categoriaActualizada = categoriaMapper.toEntity(categoriaUpdateDTO);
        Categoria categoria = categoriaService.actualizarCategoria(id, categoriaActualizada);
        CategoriaDTO categoriaDTO = categoriaMapper.toDTO(categoria);
        
        return ResponseEntity.ok(categoriaDTO);
    }

    /**
     * Eliminar una categoría (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría", 
               description = "Elimina una categoría (soft delete). Solo usuarios administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar - tiene productos asociados"),
        @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id) {
        
        log.info("DELETE /api/categorias/{} - Eliminando categoría", id);
        
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambiar estado de una categoría (activar/desactivar).
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado de categoría", 
               description = "Activa o desactiva una categoría. Solo usuarios administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "400", description = "No se puede desactivar - tiene productos asociados"),
        @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol ADMIN")
    })
    public ResponseEntity<CategoriaDTO> cambiarEstadoCategoria(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Nuevo estado (true=activo, false=inactivo)", required = true)
            @RequestParam boolean activo) {
        
        log.info("PATCH /api/categorias/{}/estado - Cambiando estado a: {}", id, activo);
        
        Categoria categoria = categoriaService.cambiarEstadoCategoria(id, activo);
        CategoriaDTO categoriaDTO = categoriaMapper.toDTO(categoria);
        
        return ResponseEntity.ok(categoriaDTO);
    }

    /**
     * Buscar categorías por nombre.
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar categorías por nombre", 
               description = "Busca categorías que contengan el texto especificado en el nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    public ResponseEntity<Page<CategoriaDTO>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true)
            @RequestParam String nombre,
            
            @Parameter(description = "Número de página (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size) {
        
        log.debug("GET /api/categorias/buscar - Buscando por nombre: {}", nombre);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre"));
        Page<Categoria> categorias = categoriaService.buscarPorNombre(nombre, pageable);
        Page<CategoriaDTO> categoriasDTO = categorias.map(categoriaMapper::toDTO);
        
        return ResponseEntity.ok(categoriasDTO);
    }

    /**
     * Obtener estadísticas de categorías.
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de categorías", 
               description = "Obtiene estadísticas sobre el uso de categorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.debug("GET /api/categorias/estadisticas - Obteniendo estadísticas");
        
        List<Object[]> categoriasConProductos = categoriaService.obtenerCategoriasConCantidadProductos();
        List<Categoria> categoriasSinProductos = categoriaService.obtenerCategoriasSinProductos();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("categoriasConProductos", categoriasConProductos);
        estadisticas.put("categoriasSinProductos", categoriaMapper.toDTOList(categoriasSinProductos));
        estadisticas.put("totalCategoriasSinProductos", categoriasSinProductos.size());
        
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtener categorías más utilizadas.
     */
    @GetMapping("/mas-utilizadas")
    @Operation(summary = "Obtener categorías más utilizadas", 
               description = "Obtiene las categorías ordenadas por cantidad de productos asociados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categorías más utilizadas obtenidas exitosamente")
    })
    public ResponseEntity<Page<CategoriaDTO>> obtenerCategoriasMasUtilizadas(
            @Parameter(description = "Número de página (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size) {
        
        log.debug("GET /api/categorias/mas-utilizadas - Obteniendo categorías más utilizadas");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Categoria> categorias = categoriaService.obtenerCategoriasMasUtilizadas(pageable);
        Page<CategoriaDTO> categoriasDTO = categorias.map(categoriaMapper::toDTO);
        
        return ResponseEntity.ok(categoriasDTO);
    }

    /**
     * Verificar si existe una categoría por nombre.
     */
    @GetMapping("/existe/{nombre}")
    @Operation(summary = "Verificar existencia de categoría", 
               description = "Verifica si existe una categoría con el nombre especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente")
    })
    public ResponseEntity<Map<String, Boolean>> verificarExistencia(
            @Parameter(description = "Nombre de la categoría", required = true)
            @PathVariable String nombre) {
        
        log.debug("GET /api/categorias/existe/{} - Verificando existencia", nombre);
        
        boolean existe = categoriaService.existeCategoriaPorNombre(nombre);
        Map<String, Boolean> resultado = new HashMap<>();
        resultado.put("existe", existe);
        
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtener conteo de productos por categoría.
     */
    @GetMapping("/{id}/productos/count")
    @Operation(summary = "Contar productos por categoría", 
               description = "Obtiene la cantidad de productos activos asociados a una categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Map<String, Long>> contarProductosPorCategoria(
            @Parameter(description = "ID de la categoría", required = true)
            @PathVariable Long id) {
        
        log.debug("GET /api/categorias/{}/productos/count - Contando productos", id);
        
        // Verificar que la categoría existe
        categoriaService.obtenerCategoriaPorId(id);
        
        Long cantidadProductos = categoriaService.contarProductosPorCategoria(id);
        Map<String, Long> resultado = new HashMap<>();
        resultado.put("cantidadProductos", cantidadProductos);
        
        return ResponseEntity.ok(resultado);
    }
}
