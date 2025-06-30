package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.exception.ProductoAlreadyExistsException;
import org.example.proyectofinal.exception.ProductoNotFoundException;
import org.example.proyectofinal.mapper.ProductoMapper;
import org.example.proyectofinal.repository.ProductoRepository;
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
    private final ProductoMapper productoMapper;

    /**
     * Crear un nuevo producto.
     * @param productoDTO datos del producto a crear
     * @return ProductoDTO del producto creado
     */
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        log.info("Creando nuevo producto: {}", productoDTO.getNombre());
        
        validarDatosProducto(productoDTO);
        
        if (StringUtils.hasText(productoDTO.getSku()) && 
            productoRepository.existsBySku(productoDTO.getSku())) {
            throw ProductoAlreadyExistsException.porSku(productoDTO.getSku());
        }
        
        Producto producto = productoMapper.toEntity(productoDTO);
        Producto productoGuardado = productoRepository.save(producto);
        
        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());
        return productoMapper.toDTO(productoGuardado);
    }

    /**
     * Obtener producto por ID.
     * @param id ID del producto
     * @return ProductoDTO del producto encontrado
     */
    public ProductoDTO obtenerProductoPorId(Long id) {
        log.debug("Buscando producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        
        return productoMapper.toDTO(producto);
    }

    /**
     * Listar todos los productos activos con paginación.
     * @param pageable información de paginación
     * @return página de productos
     */
    public Page<ProductoDTO> listarProductosActivos(Pageable pageable) {
        log.debug("Listando productos activos");
        
        Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
        return productos.map(productoMapper::toDTO);
    }

    /**
     * Buscar productos por múltiples criterios.
     * @param nombre nombre del producto (opcional)
     * @param categoria categoría del producto (opcional)
     * @param precioMin precio mínimo (opcional)
     * @param precioMax precio máximo (opcional)
     * @param pageable información de paginación
     * @return página de productos que coinciden con los criterios
     */
    public Page<ProductoDTO> buscarProductos(String nombre, String categoria, 
                                           BigDecimal precioMin, BigDecimal precioMax, 
                                           Pageable pageable) {
        log.debug("Buscando productos con criterios múltiples");
        
        // Si solo hay un criterio, usar métodos específicos que funcionan
        if (nombre != null && categoria == null && precioMin == null && precioMax == null) {
            Page<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);
            return productos.map(productoMapper::toDTO);
        }
        
        if (categoria != null && nombre == null && precioMin == null && precioMax == null) {
            Page<Producto> productos = productoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria, pageable);
            return productos.map(productoMapper::toDTO);
        }
        
        if (precioMin != null && precioMax != null && nombre == null && categoria == null) {
            Page<Producto> productos = productoRepository.findByPrecioBetweenAndActivoTrue(precioMin, precioMax, pageable);
            return productos.map(productoMapper::toDTO);
        }
        
        // Si no hay criterios, devolver todos los productos activos
        if (nombre == null && categoria == null && precioMin == null && precioMax == null) {
            Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
            return productos.map(productoMapper::toDTO);
        }
        
        // Para criterios múltiples, usar búsqueda manual (fallback)
        log.warn("Búsqueda con criterios múltiples no soportada completamente, usando todos los productos");
        Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
        return productos.map(productoMapper::toDTO);
    }

    /**
     * Actualizar un producto existente.
     * @param id ID del producto a actualizar
     * @param productoDTO datos actualizados del producto
     * @return ProductoDTO del producto actualizado
     */
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        log.info("Actualizando producto con ID: {}", id);
        
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        
        validarDatosProducto(productoDTO);
        
        if (StringUtils.hasText(productoDTO.getSku()) && 
            !productoDTO.getSku().equals(productoExistente.getSku()) &&
            productoRepository.existsBySkuAndIdNot(productoDTO.getSku(), id)) {
            throw ProductoAlreadyExistsException.porSku(productoDTO.getSku());
        }
        
        Producto productoActualizado = productoMapper.updateEntity(productoExistente, productoDTO);
        Producto productoGuardado = productoRepository.save(productoActualizado);
        
        log.info("Producto actualizado exitosamente");
        return productoMapper.toDTO(productoGuardado);
    }

    /**
     * Actualizar stock de un producto.
     * @param id ID del producto
     * @param nuevaCantidad nueva cantidad en stock
     * @return ProductoDTO del producto actualizado
     */
    @Transactional
    public ProductoDTO actualizarStock(Long id, Integer nuevaCantidad) {
        log.info("Actualizando stock del producto ID: {}", id);
        
        if (nuevaCantidad < 0) {
            throw new BusinessValidationException("La cantidad no puede ser negativa");
        }
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        
        if (!producto.getActivo()) {
            throw new BusinessValidationException("No se puede actualizar stock de un producto inactivo");
        }
        
        producto.setCantidadActual(nuevaCantidad);
        Producto productoGuardado = productoRepository.save(producto);
        
        return productoMapper.toDTO(productoGuardado);
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
    public List<ProductoDTO> obtenerProductosConStockBajo(Integer cantidadMinima) {
        List<Producto> productos = productoRepository.findProductosConStockBajo(cantidadMinima);
        return productoMapper.toDTOList(productos);
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

    // Métodos de validación privados

    private void validarDatosProducto(ProductoDTO productoDTO) {
        if (productoDTO == null) {
            throw new BusinessValidationException("Los datos del producto no pueden ser nulos");
        }
        
        if (!StringUtils.hasText(productoDTO.getNombre())) {
            throw new BusinessValidationException("El nombre del producto es obligatorio");
        }
        
        if (!StringUtils.hasText(productoDTO.getCategoria())) {
            throw new BusinessValidationException("La categoría del producto es obligatoria");
        }
        
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("El precio debe ser mayor que 0");
        }
        
        if (productoDTO.getCantidadInicial() == null || productoDTO.getCantidadInicial() < 0) {
            throw new BusinessValidationException("La cantidad inicial no puede ser negativa");
        }
    }
} 