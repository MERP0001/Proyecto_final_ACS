package org.example.proyectofinal.service;

import org.example.proyectofinal.dto.ProductoDTO;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.exception.ProductoAlreadyExistsException;
import org.example.proyectofinal.exception.ProductoNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.mapper.ProductoMapper;
import org.example.proyectofinal.repository.ProductoRepository;
import org.example.proyectofinal.validator.ProductoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private ProductoValidator productoValidator;

    @InjectMocks
    private ProductoService productoService;

    private ProductoDTO productoDTO;
    private Producto producto;

    @BeforeEach
    void setUp() {
        productoDTO = ProductoDTO.builder()
                .nombre("Laptop Test")
                .categoria("Electrónicos")
                .precio(new BigDecimal("1200.00"))
                .cantidadInicial(10)
                .sku("LAP-TST-001")
                .build();

        producto = Producto.builder()
                .id(1L)
                .nombre("Laptop Test")
                .categoria("Electrónicos")
                .precio(new BigDecimal("1200.00"))
                .cantidadInicial(10)
                .cantidadActual(10)
                .sku("LAP-TST-001")
                .activo(true)
                .build();
    }

    @Test
    void testCrearProducto_Exitoso() {
        // Arrange
        when(productoMapper.toEntity(any(ProductoDTO.class))).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);
        when(productoRepository.existsBySku(anyString())).thenReturn(false);
        doNothing().when(productoValidator).validar(any(ProductoDTO.class));

        productoDTO.setId(1L); // Simular que el DTO de respuesta tiene el ID

        // Act
        ProductoDTO resultado = productoService.crearProducto(this.productoDTO);
        System.out.println("Resultado de testCrearProducto_Exitoso: " + resultado);

        // Assert
        assertNotNull(resultado);
        assertEquals(producto.getId(), resultado.getId());
        assertEquals(producto.getNombre(), resultado.getNombre());

        verify(productoValidator, times(1)).validar(any(ProductoDTO.class));
        verify(productoRepository, times(1)).existsBySku(productoDTO.getSku());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    
    @Test
    void testCrearProducto_Falla_SkuDuplicado() {
        // Arrange
        when(productoRepository.existsBySku(productoDTO.getSku())).thenReturn(true);
        doNothing().when(productoValidator).validar(any(ProductoDTO.class));

        // Act & Assert
        Exception exception = assertThrows(ProductoAlreadyExistsException.class, () -> {
            productoService.crearProducto(productoDTO);
        });
        System.out.println("Resultado de testCrearProducto_Falla_SkuDuplicado: " + exception.getMessage());
        
        verify(productoValidator, times(1)).validar(any(ProductoDTO.class));
        verify(productoRepository, times(1)).existsBySku(productoDTO.getSku());
        verify(productoRepository, never()).save(any(Producto.class));
    }
    
    @Test
    void testObtenerProductoPorId_Exitoso() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toDTO(producto)).thenReturn(productoDTO);

        // Act
        ProductoDTO resultado = productoService.obtenerProductoPorId(1L);
        System.out.println("Resultado de testObtenerProductoPorId_Exitoso: " + resultado);

        // Assert
        assertNotNull(resultado);
        assertEquals(productoDTO.getNombre(), resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testActualizarProducto_Exitoso() {
        // Arrange
        ProductoDTO datosActualizados = ProductoDTO.builder()
                .nombre("Laptop Test Actualizada")
                .precio(new BigDecimal("1250.00"))
                .sku("LAP-TST-001")
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(datosActualizados);
        doNothing().when(productoValidator).validar(any(ProductoDTO.class));

        // Act
        ProductoDTO resultado = productoService.actualizarProducto(1L, datosActualizados);
        System.out.println("Resultado de testActualizarProducto_Exitoso: " + resultado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Laptop Test Actualizada", resultado.getNombre());
        assertEquals(0, new BigDecimal("1250.00").compareTo(resultado.getPrecio()));
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testActualizarStock_Exitoso() {
        // Arrange
        int nuevaCantidad = 25;
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoMapper.toDTO(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            return ProductoDTO.builder().cantidadActual(p.getCantidadActual()).build();
        });


        // Act
        ProductoDTO resultado = productoService.actualizarStock(1L, nuevaCantidad);
        System.out.println("Resultado de testActualizarStock_Exitoso: " + resultado);

        // Assert
        assertNotNull(resultado);
        assertEquals(nuevaCantidad, resultado.getCantidadActual());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testEliminarProducto_Exitoso() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        // Act
        productoService.eliminarProducto(1L);
        System.out.println("Resultado de testEliminarProducto_Exitoso: Producto con ID 1 marcado como inactivo.");

        // Assert
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
        assertFalse(producto.getActivo()); // Verificar que el estado del producto cambió
    }

    @Test
    void testBuscarProductos_Exitoso() {
        // Arrange
        Page<Producto> paginaProductos = new PageImpl<>(Collections.singletonList(producto));
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(paginaProductos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // Act
        Page<ProductoDTO> resultado = productoService.buscarProductos(null, Pageable.unpaged());
        System.out.println("Resultado de testBuscarProductos_Exitoso: " + resultado.getContent());

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(productoDTO.getNombre(), resultado.getContent().get(0).getNombre());
        verify(productoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    // --- Pruebas de casos de error y métodos no cubiertos ---

    @Test
    void testObtenerProductoPorId_NoEncontrado() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ProductoNotFoundException.class, () -> {
            productoService.obtenerProductoPorId(99L);
        });
        System.out.println("Resultado de testObtenerProductoPorId_NoEncontrado: " + exception.getMessage());
        assertEquals("No se encontró el producto con ID: 99", exception.getMessage());
    }

    @Test
    void testActualizarStock_Falla_ProductoInactivo() {
        // Arrange
        producto.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        Exception exception = assertThrows(BusinessValidationException.class, () -> {
            productoService.actualizarStock(1L, 50);
        });
        System.out.println("Resultado de testActualizarStock_Falla_ProductoInactivo: " + exception.getMessage());
        assertEquals("No se puede actualizar stock de un producto inactivo", exception.getMessage());
    }

    @Test
    void testActualizarStock_Falla_CantidadNegativa() {
        // Arrange
        // No es necesario el when() para findById porque la validación ocurre antes.
        
        // Act & Assert
        Exception exception = assertThrows(BusinessValidationException.class, () -> {
            productoService.actualizarStock(1L, -5);
        });
        System.out.println("Resultado de testActualizarStock_Falla_CantidadNegativa: " + exception.getMessage());
        assertEquals("La cantidad no puede ser negativa", exception.getMessage());
    }

    @Test
    void testListarProductosActivos() {
        // Arrange
        Page<Producto> paginaProductos = new PageImpl<>(Collections.singletonList(producto));
        when(productoRepository.findByActivoTrue(any(Pageable.class))).thenReturn(paginaProductos);
        when(productoMapper.toDTO(any(Producto.class))).thenReturn(productoDTO);

        // Act
        Page<ProductoDTO> resultado = productoService.listarProductosActivos(Pageable.unpaged());
        System.out.println("Resultado de testListarProductosActivos: " + resultado.getContent());
        
        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        verify(productoRepository, times(1)).findByActivoTrue(any(Pageable.class));
    }

    @Test
    void testObtenerProductosConStockBajo() {
        // Arrange
        when(productoRepository.findProductosConStockBajo(15)).thenReturn(Collections.singletonList(producto));
        when(productoMapper.toDTOList(anyList())).thenReturn(Collections.singletonList(productoDTO));

        // Act
        List<ProductoDTO> resultado = productoService.obtenerProductosConStockBajo(15);
        System.out.println("Resultado de testObtenerProductosConStockBajo: " + resultado);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findProductosConStockBajo(15);
    }

    @Test
    void testObtenerCategorias() {
        // Arrange
        List<String> categorias = List.of("Electrónicos", "Hogar");
        when(productoRepository.findDistinctCategorias()).thenReturn(categorias);

        // Act
        List<String> resultado = productoService.obtenerCategorias();
        System.out.println("Resultado de testObtenerCategorias: " + resultado);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Electrónicos", resultado.get(0));
        verify(productoRepository, times(1)).findDistinctCategorias();
    }

    @Test
    void testCalcularValorTotalInventario() {
        // Arrange
        BigDecimal valorTotal = new BigDecimal("15000.75");
        when(productoRepository.calcularValorTotalInventario()).thenReturn(valorTotal);

        // Act
        BigDecimal resultado = productoService.calcularValorTotalInventario();
        System.out.println("Resultado de testCalcularValorTotalInventario: " + resultado);
        
        // Assert
        assertEquals(0, valorTotal.compareTo(resultado));
        verify(productoRepository, times(1)).calcularValorTotalInventario();
    }
} 