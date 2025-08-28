package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.entity.Producto;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.exception.ProductoAlreadyExistsException;
import org.example.proyectofinal.exception.ProductoNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.filter.ProductoFilter;
import org.example.proyectofinal.repository.ProductoRepository;
import org.example.proyectofinal.repository.UserRepository;
import org.example.proyectofinal.validator.ProductoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias simplificadas para ProductoService.
 * Versión corregida que funciona con la implementación actual.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - Pruebas Unitarias Simplificadas")
class ProductoServiceTestSimple {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoValidator productoValidator;

    @Mock
    private MovimientoHistorialService movimientoHistorialService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoTest;
    private Categoria categoriaTest;
    private User usuarioTest;

    @BeforeEach
    void setUp() {
        // Crear objetos usando constructores simples
        categoriaTest = new Categoria();
        categoriaTest.setId(1L);
        categoriaTest.setNombre("Electrónicos");
        categoriaTest.setDescripcion("Productos electrónicos");
        categoriaTest.setActivo(true);

        usuarioTest = new User();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("admin");
        usuarioTest.setEmail("admin@example.com");
        usuarioTest.setRole(User.Role.ADMINISTRADOR);
        usuarioTest.setActivo(true);

        productoTest = new Producto();
        productoTest.setId(1L);
        productoTest.setNombre("Laptop Test");
        productoTest.setDescripcion("Laptop para pruebas");
        productoTest.setPrecio(new BigDecimal("1200.00"));
        productoTest.setCantidadInicial(10);
        productoTest.setCantidadActual(10);
        productoTest.setSku("LAP-TST-001");
        productoTest.setActivo(true);
        productoTest.setCategoria(categoriaTest);
        productoTest.setFechaCreacion(LocalDateTime.now());
        productoTest.setVersion(0L);
    }

    @Test
    @DisplayName("Crear producto - Éxito")
    void testCrearProducto_Exitoso() {
        // Arrange
        doNothing().when(productoValidator).validar(productoTest);
        when(productoRepository.existsBySku(productoTest.getSku())).thenReturn(false);
        when(productoRepository.save(productoTest)).thenReturn(productoTest);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioTest));

        // Act
        Producto resultado = productoService.crearProducto(productoTest);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Laptop Test");
        assertThat(resultado.getSku()).isEqualTo("LAP-TST-001");
        assertThat(resultado.getActivo()).isTrue();

        verify(productoValidator, times(1)).validar(productoTest);
        verify(productoRepository, times(1)).existsBySku(productoTest.getSku());
        verify(productoRepository, times(1)).save(productoTest);
    }

    @Test
    @DisplayName("Crear producto - SKU duplicado")
    void testCrearProducto_SkuDuplicado() {
        // Arrange
        doNothing().when(productoValidator).validar(productoTest);
        when(productoRepository.existsBySku(productoTest.getSku())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productoService.crearProducto(productoTest))
                .isInstanceOf(ProductoAlreadyExistsException.class);

        verify(productoValidator, times(1)).validar(productoTest);
        verify(productoRepository, times(1)).existsBySku(productoTest.getSku());
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener producto por ID - Éxito")
    void testObtenerProductoPorId_Exitoso() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

        // Act
        Producto resultado = productoService.obtenerProductoPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Laptop Test");

        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtener producto por ID - No encontrado")
    void testObtenerProductoPorId_NoEncontrado() {
        // Arrange
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.obtenerProductoPorId(999L))
                .isInstanceOf(ProductoNotFoundException.class);

        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Listar productos activos")
    void testListarProductosActivos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Producto> productos = Arrays.asList(productoTest);
        Page<Producto> page = new PageImpl<>(productos, pageable, 1);

        when(productoRepository.findByActivoTrue(pageable)).thenReturn(page);

        // Act
        Page<Producto> resultado = productoService.listarProductosActivos(pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getNumber()).isEqualTo(0);

        verify(productoRepository, times(1)).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("Buscar productos con filtros")
    void testBuscarProductos() {
        // Arrange
        ProductoFilter filtro = new ProductoFilter();
        filtro.setNombre("Laptop");

        Pageable pageable = PageRequest.of(0, 10);
        List<Producto> productos = Arrays.asList(productoTest);
        Page<Producto> page = new PageImpl<>(productos, pageable, 1);

        when(productoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // Act
        Page<Producto> resultado = productoService.buscarProductos(filtro, pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).contains("Laptop");

        verify(productoRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Obtener producto por SKU")
    void testObtenerProductoPorSku() {
        // Arrange
        when(productoRepository.findBySku("LAP-TST-001")).thenReturn(Optional.of(productoTest));

        // Act
        Producto resultado = productoService.obtenerProductoPorSku("LAP-TST-001");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getSku()).isEqualTo("LAP-TST-001");

        verify(productoRepository, times(1)).findBySku("LAP-TST-001");
    }

    @Test
    @DisplayName("Actualizar stock - Éxito")
    void testActualizarStock_Exitoso() {
        // Arrange
        Integer nuevaCantidad = 25;
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioTest));

        // Act
        Producto resultado = productoService.actualizarStock(1L, nuevaCantidad, "admin");

        // Assert
        assertThat(resultado).isNotNull();

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Actualizar stock - Producto inactivo")
    void testActualizarStock_ProductoInactivo() {
        // Arrange
        productoTest.setActivo(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));

        // Act & Assert
        assertThatThrownBy(() -> productoService.actualizarStock(1L, 25, "admin"))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("No se puede actualizar stock de un producto inactivo");

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar producto (soft delete) - Éxito")
    void testEliminarProducto_Exitoso() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

        // Act
        productoService.eliminarProducto(1L);

        // Assert
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Calcular valor total inventario")
    void testCalcularValorTotalInventario() {
        // Arrange
        BigDecimal valorTotal = new BigDecimal("15000.75");
        when(productoRepository.calcularValorTotalInventario()).thenReturn(valorTotal);

        // Act
        BigDecimal resultado = productoService.calcularValorTotalInventario();

        // Assert
        assertThat(resultado).isEqualByComparingTo(valorTotal);
        verify(productoRepository, times(1)).calcularValorTotalInventario();
    }
}