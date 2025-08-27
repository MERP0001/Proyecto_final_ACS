package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.validator.CategoriaValidator;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CategoriaService.
 * Utiliza Mockito para simular dependencias y validar comportamientos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService - Pruebas Unitarias")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaValidator categoriaValidator;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaTest;
    private Categoria categoriaTest2;

    @BeforeEach
    void setUp() {
        categoriaTest = Categoria.builder()
                .id(1L)
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        categoriaTest2 = Categoria.builder()
                .id(2L)
                .nombre("Hogar")
                .descripcion("Productos para el hogar")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Operaciones CRUD Básicas")
    class OperacionesCRUD {

        @Test
        @DisplayName("Crear categoría - Éxito")
        void testCrearCategoria_Exitoso() {
            // Arrange
            doNothing().when(categoriaValidator).validar(categoriaTest);
            when(categoriaRepository.existsByNombreIgnoreCase(categoriaTest.getNombre())).thenReturn(false);
            when(categoriaRepository.save(categoriaTest)).thenReturn(categoriaTest);

            // Act
            Categoria resultado = categoriaService.crearCategoria(categoriaTest);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos");
            assertThat(resultado.getActivo()).isTrue();

            verify(categoriaValidator, times(1)).validar(categoriaTest);
            verify(categoriaRepository, times(1)).existsByNombreIgnoreCase(categoriaTest.getNombre());
            verify(categoriaRepository, times(1)).save(categoriaTest);
        }

        @Test
        @DisplayName("Crear categoría - Nombre duplicado")
        void testCrearCategoria_NombreDuplicado() {
            // Arrange
            doNothing().when(categoriaValidator).validar(categoriaTest);
            when(categoriaRepository.existsByNombreIgnoreCase(categoriaTest.getNombre())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.crearCategoria(categoriaTest))
                    .isInstanceOf(CategoriaAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe una categoría con el nombre: Electrónicos");

            verify(categoriaValidator, times(1)).validar(categoriaTest);
            verify(categoriaRepository, times(1)).existsByNombreIgnoreCase(categoriaTest.getNombre());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Obtener categoría por ID - Éxito")
        void testObtenerCategoriaPorId_Exitoso() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));

            // Act
            Categoria resultado = categoriaService.obtenerCategoriaPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos");

            verify(categoriaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Obtener categoría por ID - No encontrada")
        void testObtenerCategoriaPorId_NoEncontrada() {
            // Arrange
            when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.obtenerCategoriaPorId(999L))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessageContaining("No se encontró la categoría con ID: 999");

            verify(categoriaRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Actualizar categoría - Éxito")
        void testActualizarCategoria_Exitoso() {
            // Arrange
            Categoria categoriaActualizada = Categoria.builder()
                    .id(1L)
                    .nombre("Electrónicos Actualizados")
                    .descripcion("Nueva descripción")
                    .activo(true)
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
            doNothing().when(categoriaValidator).validar(categoriaActualizada);
            when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Electrónicos Actualizados", 1L)).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);

            // Act
            Categoria resultado = categoriaService.actualizarCategoria(1L, categoriaActualizada);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos Actualizados");

            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaValidator, times(1)).validar(categoriaActualizada);
            verify(categoriaRepository, times(1)).save(any(Categoria.class));
        }

        @Test
        @DisplayName("Eliminar categoría (soft delete) - Éxito")
        void testEliminarCategoria_Exitoso() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
            when(categoriaRepository.tieneProductosActivos(1L)).thenReturn(false);
            doNothing().when(categoriaValidator).validarEliminacion(categoriaTest, false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaTest);

            // Act
            categoriaService.eliminarCategoria(1L);

            // Assert
            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaRepository, times(1)).tieneProductosActivos(1L);
            verify(categoriaValidator, times(1)).validarEliminacion(categoriaTest, false);
            verify(categoriaRepository, times(1)).save(argThat(categoria -> !categoria.getActivo()));
        }
    }

    @Nested
    @DisplayName("Operaciones de Consulta")
    class OperacionesConsulta {

        @Test
        @DisplayName("Listar todas las categorías activas")
        void testObtenerCategoriasActivas() {
            // Arrange
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            when(categoriaRepository.findByActivoTrueOrderByNombre()).thenReturn(categorias);

            // Act
            List<Categoria> resultado = categoriaService.obtenerCategoriasActivas();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado).contains(categoriaTest, categoriaTest2);

            verify(categoriaRepository, times(1)).findByActivoTrueOrderByNombre();
        }

        @Test
        @DisplayName("Listar categorías con paginación")
        void testListarCategoriasActivas() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

            when(categoriaRepository.findByActivoTrue(pageable)).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.listarCategoriasActivas(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getNumber()).isEqualTo(0);

            verify(categoriaRepository, times(1)).findByActivoTrue(pageable);
        }

        @Test
        @DisplayName("Buscar categorías con filtros")
        void testBuscarCategorias() {
            // Arrange
            CategoriaFilter filtro = CategoriaFilter.builder()
                    .nombre("Elect")
                    .activo(true)
                    .build();

            Pageable pageable = PageRequest.of(0, 10);
            List<Categoria> categorias = Arrays.asList(categoriaTest);
            Page<Categoria> page = new PageImpl<>(categorias, pageable, 1);

            when(categoriaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.buscarCategorias(filtro, pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNombre()).contains("Elect");

            verify(categoriaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Buscar categoría por nombre - Existente")
        void testObtenerCategoriaPorNombre_Existente() {
            // Arrange
            when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("electrónicos"))
                    .thenReturn(Optional.of(categoriaTest));

            // Act
            Categoria resultado = categoriaService.obtenerCategoriaPorNombre("electrónicos");

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos");

            verify(categoriaRepository, times(1)).findByNombreIgnoreCaseAndActivoTrue("electrónicos");
        }

        @Test
        @DisplayName("Buscar categoría por nombre - No existente")
        void testObtenerCategoriaPorNombre_NoExistente() {
            // Arrange
            when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("inexistente"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.obtenerCategoriaPorNombre("inexistente"))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessageContaining("No se encontró la categoría con nombre: inexistente");

            verify(categoriaRepository, times(1)).findByNombreIgnoreCaseAndActivoTrue("inexistente");
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class ValidacionesNegocio {

        @Test
        @DisplayName("Verificar si categoría existe por nombre")
        void testExisteCategoriaPorNombre() {
            // Arrange
            when(categoriaRepository.existsByNombreIgnoreCase("Electrónicos")).thenReturn(true);
            when(categoriaRepository.existsByNombreIgnoreCase("Inexistente")).thenReturn(false);

            // Act & Assert
            assertThat(categoriaService.existeCategoriaPorNombre("Electrónicos")).isTrue();
            assertThat(categoriaService.existeCategoriaPorNombre("Inexistente")).isFalse();

            verify(categoriaRepository, times(1)).existsByNombreIgnoreCase("Electrónicos");
            verify(categoriaRepository, times(1)).existsByNombreIgnoreCase("Inexistente");
        }

        @Test
        @DisplayName("Contar productos por categoría")
        void testContarProductosPorCategoria() {
            // Arrange
            when(categoriaRepository.countProductosActivosByCategoria(1L)).thenReturn(5L);

            // Act
            Long resultado = categoriaService.contarProductosPorCategoria(1L);

            // Assert
            assertThat(resultado).isEqualTo(5L);

            verify(categoriaRepository, times(1)).countProductosActivosByCategoria(1L);
        }

        @Test
        @DisplayName("Cambiar estado categoría - Desactivar con productos")
        void testCambiarEstadoCategoria_DesactivarConProductos() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
            when(categoriaRepository.tieneProductosActivos(1L)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.cambiarEstadoCategoria(1L, false))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("No se puede desactivar la categoría");

            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaRepository, times(1)).tieneProductosActivos(1L);
        }
    }

    @Nested
    @DisplayName("Operaciones Avanzadas")
    class OperacionesAvanzadas {

        @Test
        @DisplayName("Obtener categorías más utilizadas")
        void testObtenerCategoriasMasUtilizadas() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5);
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

            when(categoriaRepository.findCategoriasMasUtilizadas(pageable)).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.obtenerCategoriasMasUtilizadas(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            verify(categoriaRepository, times(1)).findCategoriasMasUtilizadas(pageable);
        }

        @Test
        @DisplayName("Obtener categorías sin productos")
        void testObtenerCategoriasSinProductos() {
            // Arrange
            List<Categoria> categoriasSinProductos = Arrays.asList(categoriaTest2);
            when(categoriaRepository.findCategoriasSinProductos()).thenReturn(categoriasSinProductos);

            // Act
            List<Categoria> resultado = categoriaService.obtenerCategoriasSinProductos();

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Hogar");
            verify(categoriaRepository, times(1)).findCategoriasSinProductos();
        }
    }

    @Nested
    @DisplayName("Casos Edge y Errores")
    class CasosEdgeYErrores {

        @Test
        @DisplayName("Crear categoría - Entrada nula")
        void testCrearCategoria_EntradaNula() {
            // Arrange
            doThrow(new BusinessValidationException("Los datos de la categoría no pueden ser nulos"))
                    .when(categoriaValidator).validar(null);

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.crearCategoria(null))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessage("Los datos de la categoría no pueden ser nulos");

            verify(categoriaValidator, times(1)).validar(null);
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Buscar categorías - Filtro nulo")
        void testBuscarCategorias_FiltroNulo() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

            when(categoriaRepository.findByActivoTrue(pageable)).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.buscarCategorias(null, pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            verify(categoriaRepository, times(1)).findByActivoTrue(pageable);
        }
    }
}
