package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.specification.CategoriaSpecification;
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
            doNothing().when(categoriaValidator).validarCreacion(categoriaTest);
            when(categoriaRepository.save(categoriaTest)).thenReturn(categoriaTest);

            // Act
            Categoria resultado = categoriaService.crearCategoria(categoriaTest);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos");
            assertThat(resultado.isActivo()).isTrue();

            verify(categoriaValidator, times(1)).validarCreacion(categoriaTest);
            verify(categoriaRepository, times(1)).save(categoriaTest);
        }

        @Test
        @DisplayName("Crear categoría - Nombre duplicado")
        void testCrearCategoria_NombreDuplicado() {
            // Arrange
            doThrow(new CategoriaAlreadyExistsException("Categoría ya existe"))
                    .when(categoriaValidator).validarCreacion(categoriaTest);

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.crearCategoria(categoriaTest))
                    .isInstanceOf(CategoriaAlreadyExistsException.class)
                    .hasMessage("Categoría ya existe");

            verify(categoriaValidator, times(1)).validarCreacion(categoriaTest);
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
                    .hasMessage("Categoría con ID 999 no encontrada");

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
            doNothing().when(categoriaValidator).validarActualizacion(1L, categoriaActualizada);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);

            // Act
            Categoria resultado = categoriaService.actualizarCategoria(1L, categoriaActualizada);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Electrónicos Actualizados");

            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaValidator, times(1)).validarActualizacion(1L, categoriaActualizada);
            verify(categoriaRepository, times(1)).save(any(Categoria.class));
        }

        @Test
        @DisplayName("Eliminar categoría (soft delete) - Éxito")
        void testEliminarCategoria_Exitoso() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
            doNothing().when(categoriaValidator).validarEliminacion(1L);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaTest);

            // Act
            categoriaService.eliminarCategoria(1L);

            // Assert
            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaValidator, times(1)).validarEliminacion(1L);
            verify(categoriaRepository, times(1)).save(argThat(categoria -> !categoria.isActivo()));
        }
    }

    @Nested
    @DisplayName("Operaciones de Consulta")
    class OperacionesConsulta {

        @Test
        @DisplayName("Listar todas las categorías activas")
        void testListarCategoriasActivas() {
            // Arrange
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            when(categoriaRepository.findByActivoTrueOrderByNombre()).thenReturn(categorias);

            // Act
            List<Categoria> resultado = categoriaService.listarCategoriasActivas();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado).contains(categoriaTest, categoriaTest2);

            verify(categoriaRepository, times(1)).findByActivoTrueOrderByNombre();
        }

        @Test
        @DisplayName("Listar categorías con paginación")
        void testListarCategoriasConPaginacion() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
            Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

            when(categoriaRepository.findByActivoTrue(pageable)).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.listarCategoriasConPaginacion(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getNumber()).isEqualTo(0);

            verify(categoriaRepository, times(1)).findByActivoTrue(pageable);
        }

        @Test
        @DisplayName("Buscar categorías con filtros")
        void testBuscarCategoriasConFiltros() {
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
            Page<Categoria> resultado = categoriaService.buscarCategoriasConFiltros(filtro, pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNombre()).contains("Elect");

            verify(categoriaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Buscar categoría por nombre - Existente")
        void testBuscarCategoriaPorNombre_Existente() {
            // Arrange
            when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("electrónicos"))
                    .thenReturn(Optional.of(categoriaTest));

            // Act
            Optional<Categoria> resultado = categoriaService.buscarCategoriaPorNombre("electrónicos");

            // Assert
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNombre()).isEqualTo("Electrónicos");

            verify(categoriaRepository, times(1)).findByNombreIgnoreCaseAndActivoTrue("electrónicos");
        }

        @Test
        @DisplayName("Buscar categoría por nombre - No existente")
        void testBuscarCategoriaPorNombre_NoExistente() {
            // Arrange
            when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("inexistente"))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Categoria> resultado = categoriaService.buscarCategoriaPorNombre("inexistente");

            // Assert
            assertThat(resultado).isEmpty();

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
            when(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("Electrónicos"))
                    .thenReturn(true);
            when(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("Inexistente"))
                    .thenReturn(false);

            // Act & Assert
            assertThat(categoriaService.existeCategoriaPorNombre("Electrónicos")).isTrue();
            assertThat(categoriaService.existeCategoriaPorNombre("Inexistente")).isFalse();

            verify(categoriaRepository, times(1)).existsByNombreIgnoreCaseAndActivoTrue("Electrónicos");
            verify(categoriaRepository, times(1)).existsByNombreIgnoreCaseAndActivoTrue("Inexistente");
        }

        @Test
        @DisplayName("Contar total de categorías activas")
        void testContarCategoriasActivas() {
            // Arrange
            when(categoriaRepository.countByActivoTrue()).thenReturn(5L);

            // Act
            Long resultado = categoriaService.contarCategoriasActivas();

            // Assert
            assertThat(resultado).isEqualTo(5L);

            verify(categoriaRepository, times(1)).countByActivoTrue();
        }

        @Test
        @DisplayName("Verificar si categoría tiene productos")
        void testTieneProductos() {
            // Arrange
            when(categoriaRepository.tieneProductosActivos(1L)).thenReturn(true);
            when(categoriaRepository.tieneProductosActivos(2L)).thenReturn(false);

            // Act & Assert
            assertThat(categoriaService.tieneProductos(1L)).isTrue();
            assertThat(categoriaService.tieneProductos(2L)).isFalse();

            verify(categoriaRepository, times(1)).tieneProductosActivos(1L);
            verify(categoriaRepository, times(1)).tieneProductosActivos(2L);
        }
    }

    @Nested
    @DisplayName("Operaciones Avanzadas")
    class OperacionesAvanzadas {

        @Test
        @DisplayName("Activar categoría")
        void testActivarCategoria() {
            // Arrange
            Categoria categoriaInactiva = Categoria.builder()
                    .id(1L)
                    .nombre("Electrónicos")
                    .activo(false)
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaInactiva));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaInactiva);

            // Act
            Categoria resultado = categoriaService.activarCategoria(1L);

            // Assert
            assertThat(resultado.isActivo()).isTrue();

            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaRepository, times(1)).save(argThat(categoria -> categoria.isActivo()));
        }

        @Test
        @DisplayName("Desactivar categoría")
        void testDesactivarCategoria() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
            doNothing().when(categoriaValidator).validarEliminacion(1L);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaTest);

            // Act
            Categoria resultado = categoriaService.desactivarCategoria(1L);

            // Assert
            assertThat(resultado.isActivo()).isFalse();

            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaValidator, times(1)).validarEliminacion(1L);
            verify(categoriaRepository, times(1)).save(argThat(categoria -> !categoria.isActivo()));
        }

        @Test
        @DisplayName("Obtener categorías más utilizadas")
        void testObtenerCategoriasMasUtilizadas() {
            // Arrange
            Object[] categoria1 = {categoriaTest, 10L};
            Object[] categoria2 = {categoriaTest2, 5L};
            List<Object[]> resultados = Arrays.asList(categoria1, categoria2);

            when(categoriaRepository.findCategoriasConConteoProductos()).thenReturn(resultados);

            // Act
            List<Object[]> resultado = categoriaService.obtenerCategoriasMasUtilizadas();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0)[1]).isEqualTo(10L);
            assertThat(resultado.get(1)[1]).isEqualTo(5L);

            verify(categoriaRepository, times(1)).findCategoriasConConteoProductos();
        }
    }

    @Nested
    @DisplayName("Casos Edge y Errores")
    class CasosEdgeYErrores {

        @Test
        @DisplayName("Crear categoría - Entrada nula")
        void testCrearCategoria_EntradaNula() {
            // Act & Assert
            assertThatThrownBy(() -> categoriaService.crearCategoria(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La categoría no puede ser nula");

            verify(categoriaValidator, never()).validarCreacion(any());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Actualizar categoría - ID nulo")
        void testActualizarCategoria_IdNulo() {
            // Act & Assert
            assertThatThrownBy(() -> categoriaService.actualizarCategoria(null, categoriaTest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El ID no puede ser nulo");

            verify(categoriaRepository, never()).findById(any());
            verify(categoriaValidator, never()).validarActualizacion(any(), any());
        }

        @Test
        @DisplayName("Eliminar categoría - ID inexistente")
        void testEliminarCategoria_IdInexistente() {
            // Arrange
            when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoriaService.eliminarCategoria(999L))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessage("Categoría con ID 999 no encontrada");

            verify(categoriaRepository, times(1)).findById(999L);
            verify(categoriaValidator, never()).validarEliminacion(any());
        }
    }
}
