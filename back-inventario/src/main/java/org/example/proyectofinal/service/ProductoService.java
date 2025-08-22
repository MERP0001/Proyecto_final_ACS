package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.entity.TipoMovimiento;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.exception.ProductoAlreadyExistsException;
import org.example.proyectofinal.exception.ProductoNotFoundException;
import org.example.proyectofinal.filter.ProductoFilter;
import org.example.proyectofinal.mapper.ProductoMapper;
import org.example.proyectofinal.repository.ProductoRepository;
import org.example.proyectofinal.repository.UserRepository;
import org.example.proyectofinal.specification.ProductoSpecification;
import org.example.proyectofinal.validator.ProductoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para la gestión de productos.
 * Contiene toda la lógica de negocio para operaciones CRUD y validaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoValidator productoValidator;
    private final MovimientoHistorialService movimientoHistorialService;
    private final UserRepository userRepository;

    /**
     * Crear un nuevo producto.
     * @param producto datos del producto a crear
     * @return Producto del producto creado
     */
    @Transactional
    public Producto crearProducto(Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());

        productoValidator.validar(producto);

        if (StringUtils.hasText(producto.getSku()) &&
            productoRepository.existsBySku(producto.getSku())) {
            throw ProductoAlreadyExistsException.porSku(producto.getSku());
        }

        Producto productoGuardado = productoRepository.save(producto);
        
        // Registrar movimiento de entrada inicial
        // FIXME: Obtener el usuario autenticado desde el contexto de seguridad.
        User adminUser = userRepository.findByUsername("admin").orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));
        movimientoHistorialService.registrarMovimiento(
            productoGuardado, 
            adminUser, 
            TipoMovimiento.ENTRADA, 
            productoGuardado.getCantidadActual()
        );

        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());
        return productoGuardado;
    }

    /**
     * Obtener producto por ID.
     * @param id ID del producto
     * @return Producto del producto encontrado
     */
    public Producto obtenerProductoPorId(Long id) {
        log.debug("Buscando producto con ID: {}", id);

        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    /**
     * Listar todos los productos activos con paginación.
     * @param pageable información de paginación
     * @return página de productos
     */
    public Page<Producto> listarProductosActivos(Pageable pageable) {
        log.debug("Listando productos activos");

        return productoRepository.findByActivoTrue(pageable);
    }

    /**
     * Buscar productos por múltiples criterios.
     * @param filter filtros para la búsqueda
     * @param pageable información de paginación
     * @return página de productos que coinciden con los criterios
     */
    public Page<Producto> buscarProductos(ProductoFilter filter, Pageable pageable) {
        log.debug("Buscando productos con filtros dinámicos");
        var spec = ProductoSpecification.conFiltros(filter);
        return productoRepository.findAll(spec, pageable);
    }

    /**
     * Actualizar un producto existente.
     * @param id ID del producto a actualizar
     * @param productoActualizado datos actualizados del producto
     * @return Producto del producto actualizado
     */
    @Transactional
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        log.info("Actualizando producto con ID: {}", id);

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        productoValidator.validar(productoActualizado);

        if (StringUtils.hasText(productoActualizado.getSku()) &&
            !productoActualizado.getSku().equals(productoExistente.getSku()) &&
            productoRepository.existsBySkuAndIdNot(productoActualizado.getSku(), id)) {
            throw ProductoAlreadyExistsException.porSku(productoActualizado.getSku());
        }

        // Mapeo manual de campos actualizables
        productoExistente.setSku(productoActualizado.getSku());
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setCategoria(productoActualizado.getCategoria());
        // La cantidad se maneja en su propio método (actualizarStock)
        // El estado (activo) se maneja en eliminarProducto

        Producto productoGuardado = productoRepository.save(productoExistente);

        log.info("Producto actualizado exitosamente");
        return productoGuardado;
    }

    /**
     * Actualizar stock de un producto.
     * @param id ID del producto
     * @param nuevaCantidad nueva cantidad en stock
     * @return Producto del producto actualizado
     */
    @Transactional
    public Producto actualizarStock(Long id, Integer nuevaCantidad) {
        log.info("Actualizando stock del producto ID: {}", id);

        if (nuevaCantidad < 0) {
            throw new BusinessValidationException("La cantidad no puede ser negativa");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        
        if (!producto.getActivo()) {
            throw new BusinessValidationException("No se puede actualizar stock de un producto inactivo");
        }

        int cantidadAnterior = producto.getCantidadActual();
        int diferencia = nuevaCantidad - cantidadAnterior;

        if (diferencia == 0) {
            log.warn("La nueva cantidad es la misma que la actual. No se realiza ninguna operación.");
            return producto; // No hay cambios
        }

        TipoMovimiento tipo = diferencia > 0 ? TipoMovimiento.AJUSTE_POSITIVO : TipoMovimiento.AJUSTE_NEGATIVO;
        int cantidadMovimiento = Math.abs(diferencia);
        
        producto.setCantidadActual(nuevaCantidad);
        Producto productoGuardado = productoRepository.save(producto);
        
        // Registrar el ajuste de stock
        // FIXME: Obtener el usuario autenticado desde el contexto de seguridad.
        User adminUser = userRepository.findByUsername("admin").orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));
        movimientoHistorialService.registrarMovimiento(
            productoGuardado, 
            adminUser,
            tipo, 
            cantidadMovimiento
        );
        
        return productoGuardado;
    }

    /**
     * Eliminar un producto (soft delete).
     * @param id ID del producto a eliminar
     */
    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        producto.setActivo(false);
        productoRepository.save(producto);

        log.info("Producto eliminado exitosamente");
    }

    /**
     * Obtener productos con stock bajo.
     * @param cantidadMinima cantidad mínima para considerar stock bajo
     * @return lista de productos con stock bajo
     */
    public List<Producto> obtenerProductosConStockBajo(Integer cantidadMinima) {
        return productoRepository.findProductosConStockBajo(cantidadMinima);
    }

    /**
     * Obtener todas las categorías únicas.
     * @return lista de categorías
     */
    public List<String> obtenerCategorias() {
        return productoRepository.findDistinctCategorias();
    }

    /**
     * Calcular valor total del inventario.
     * @return valor total del inventario
     */
    public BigDecimal calcularValorTotalInventario() {
        return productoRepository.calcularValorTotalInventario();
    }

} 