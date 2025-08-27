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
import org.junit.jupiter.api.Nested;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoService.
 * Utiliza Mockito para simular dependencias y validar comportamientos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - Pruebas Unitarias")
class ProductoServiceTest {

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
    private Producto productoTest2;
    private Categoria categoriaTest;
    private User usuarioTest;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(1L)
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos")
                .activo(true)
                .build();

        usuarioTest = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .role(User.Role.ADMINISTRADOR)
                .activo(true)
                .build();

        productoTest = Producto.builder()
                .id(1L)
                .nombre("Laptop Test")
                .descripcion("Laptop para pruebas")
                .precio(new BigDecimal("1200.00"))
                .cantidadInicial(10)
                .cantidadActual(10)
                .stockMinimo(5)
                .sku("LAP-TST-001")
                .activo(true)
                .categoria(categoriaTest)
                .categoriaLegacy("Electrónicos")
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        productoTest2 = Producto.builder()
                .id(2L)
                .nombre("Mouse Test")
                .descripcion("Mouse para pruebas")
                .precio(new BigDecimal("50.00"))
                .cantidadInicial(20)
                .cantidadActual(15)
                .stockMinimo(10)
                .sku("MOU-TST-002")
                .activo(true)
                .categoria(categoriaTest)
                .categoriaLegacy("Electrónicos")
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Operaciones CRUD Básicas")
    class OperacionesCRUD {

        @Test
        @DisplayName("Crear producto - Éxito")
        void testCrearProducto_Exitoso() {
            // Arrange
            doNothing().when(productoValidator).validar(productoTest);
            when(productoRepository.existsBySku(productoTest.getSku())).thenReturn(false);
            when(productoRepository.save(productoTest)).thenReturn(productoTest);

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
                    .isInstanceOf(ProductoAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe un producto con el SKU: LAP-TST-001");

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
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("No se encontró el producto con ID: 999");

            verify(productoRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Actualizar producto - Éxito")
        void testActualizarProducto_Exitoso() {
            // Arrange
            Producto productoActualizado = Producto.builder()
                    .id(1L)
                    .nombre("Laptop Test Actualizada")
                    .descripcion("Nueva descripción")
                    .precio(new BigDecimal("1300.00"))
                    .sku("LAP-TST-001")
                    .activo(true)
                    .build();

            when(productoRepository.findById(1L)).thenReturn(Optional.of(productoTest));
            doNothing().when(productoValidator).validar(productoActualizado);
            when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

            // Act
            Producto resultado = productoService.actualizarProducto(1L, productoActualizado);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Laptop Test Actualizada");

            verify(productoRepository, times(1)).findById(1L);
            verify(productoValidator, times(1)).validar(productoActualizado);
            verify(productoRepository, times(1)).save(any(Producto.class));
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
            verify(productoRepository, times(1)).save(argThat(producto -> !producto.getActivo()));
        }
    }

    @Nested
    @DisplayName("Operaciones de Consulta")
    class OperacionesConsulta {

        @Test
        @DisplayName("Listar productos activos")
        void testListarProductosActivos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Producto> productos = Arrays.asList(productoTest, productoTest2);
            Page<Producto> page = new PageImpl<>(productos, pageable, 2);

            when(productoRepository.findByActivoTrue(pageable)).thenReturn(page);

            // Act
            Page<Producto> resultado = productoService.listarProductosActivos(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getNumber()).isEqualTo(0);

            verify(productoRepository, times(1)).findByActivoTrue(pageable);
        }

        @Test
        @DisplayName("Buscar productos con filtros")
        void testBuscarProductos() {
            // Arrange
            ProductoFilter filtro = ProductoFilter.builder()
                    .nombre("Laptop")
                    .activo(true)
                    .build();

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
        @DisplayName("Obtener productos con stock bajo")
        void testObtenerProductosConStockBajo() {
            // Arrange
            List<Producto> productosStockBajo = Arrays.asList(productoTest2);
            when(productoRepository.findProductosConStockBajo(15)).thenReturn(productosStockBajo);

            // Act
            List<Producto> resultado = productoService.obtenerProductosConStockBajo(15);

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getCantidadActual()).isLessThan(16);
            verify(productoRepository, times(1)).findProductosConStockBajo(15);
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
    }

    @Nested
    @DisplayName("Operaciones de Stock")
    class OperacionesStock {

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
            assertThat(resultado.getCantidadActual()).isEqualTo(nuevaCantidad);

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
        @DisplayName("Actualizar stock - Cantidad negativa")
        void testActualizarStock_CantidadNegativa() {
            // Act & Assert
            assertThatThrownBy(() -> productoService.actualizarStock(1L, -5, "admin"))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("La cantidad no puede ser negativa");

            verify(productoRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("Operaciones Avanzadas")
    class OperacionesAvanzadas {

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

        @Test
        @DisplayName("Obtener categorías distintas")
        void testObtenerCategorias() {
            // Arrange
            List<String> categorias = Arrays.asList("Electrónicos", "Hogar", "Deportes");
            when(productoRepository.findDistinctCategorias()).thenReturn(categorias);

            // Act
            List<String> resultado = productoService.obtenerCategorias();

            // Assert
            assertThat(resultado).hasSize(3);
            assertThat(resultado).contains("Electrónicos", "Hogar", "Deportes");
            verify(productoRepository, times(1)).findDistinctCategorias();
        }

        @Test
        @DisplayName("Crear producto con categoría")
        void testCrearProductoConCategoria() {
            // Arrange
            when(categoriaService.obtenerCategoriaPorId(1L)).thenReturn(categoriaTest);
            doNothing().when(productoValidator).validar(productoTest);
            when(productoRepository.existsBySku(productoTest.getSku())).thenReturn(false);
            when(productoRepository.save(productoTest)).thenReturn(productoTest);

            // Act
            Producto resultado = productoService.crearProductoConCategoria(productoTest, 1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getCategoria()).isEqualTo(categoriaTest);
            assertThat(resultado.getCategoriaLegacy()).isEqualTo("Electrónicos");

            verify(categoriaService, times(1)).obtenerCategoriaPorId(1L);
            verify(productoRepository, times(1)).save(productoTest);
        }
    }

    @Nested
    @DisplayName("Casos Edge y Errores")
    class CasosEdgeYErrores {

        @Test
        @DisplayName("Crear producto - Entrada nula")
        void testCrearProducto_EntradaNula() {
            // Arrange
            doThrow(new BusinessValidationException("Los datos del producto no pueden ser nulos"))
                    .when(productoValidator).validar(null);

            // Act & Assert
            assertThatThrownBy(() -> productoService.crearProducto(null))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessage("Los datos del producto no pueden ser nulos");

            verify(productoValidator, times(1)).validar(null);
            verify(productoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Buscar productos - Filtro nulo")
        void testBuscarProductos_FiltroNulo() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Producto> productos = Arrays.asList(productoTest, productoTest2);
            Page<Producto> page = new PageImpl<>(productos, pageable, 2);

            when(productoRepository.findByActivoTrue(pageable)).thenReturn(page);

            // Act
            Page<Producto> resultado = productoService.buscarProductos(null, pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            verify(productoRepository, times(1)).findByActivoTrue(pageable);
        }

        @Test
        @DisplayName("Obtener producto por SKU - No encontrado")
        void testObtenerProductoPorSku_NoEncontrado() {
            // Arrange
            when(productoRepository.findBySku("INEXISTENTE")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> productoService.obtenerProductoPorSku("INEXISTENTE"))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("No se encontró el producto con SKU: INEXISTENTE");

            verify(productoRepository, times(1)).findBySku("INEXISTENTE");
        }
    }
} 