package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.example.proyectofinal.specification.CategoriaSpecification;
import org.example.proyectofinal.validator.CategoriaValidator;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias simplificadas para CategoriaService.
 * Versión corregida que funciona con la implementación actual.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService - Pruebas Unitarias Simplificadas")
class CategoriaServiceTestSimple {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaValidator categoriaValidator;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaTest;
    private Categoria categoriaTest2;
    private CategoriaFilter filtroTest;

    @BeforeEach
    void setUp() {
        // Crear objetos usando constructores simples
        categoriaTest = new Categoria();
        categoriaTest.setId(1L);
        categoriaTest.setNombre("Electrónicos");
        categoriaTest.setDescripcion("Dispositivos electrónicos y gadgets");
        categoriaTest.setActivo(true);
        categoriaTest.setFechaCreacion(LocalDateTime.now());
        categoriaTest.setVersion(1L);

        categoriaTest2 = new Categoria();
        categoriaTest2.setId(2L);
        categoriaTest2.setNombre("Hogar");
        categoriaTest2.setDescripcion("Artículos para el hogar");
        categoriaTest2.setActivo(true);
        categoriaTest2.setFechaCreacion(LocalDateTime.now());
        categoriaTest2.setVersion(1L);

        filtroTest = new CategoriaFilter();
        filtroTest.setNombre("Electr");
        filtroTest.setActivo(true);
    }

    @Test
    @DisplayName("Crear categoría - Éxito")
    void testCrearCategoria_Exitoso() {
        // Arrange
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre("Nueva Categoría");
        nuevaCategoria.setDescripcion("Descripción de prueba");
        nuevaCategoria.setActivo(true);

        doNothing().when(categoriaValidator).validar(nuevaCategoria);
        when(categoriaRepository.existsByNombreIgnoreCase("Nueva Categoría")).thenReturn(false);
        when(categoriaRepository.save(nuevaCategoria)).thenReturn(categoriaTest);

        // Act
        Categoria resultado = categoriaService.crearCategoria(nuevaCategoria);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Electrónicos");

        verify(categoriaValidator, times(1)).validar(nuevaCategoria);
        verify(categoriaRepository, times(1)).existsByNombreIgnoreCase("Nueva Categoría");
        verify(categoriaRepository, times(1)).save(nuevaCategoria);
    }

    @Test
    @DisplayName("Crear categoría - Nombre duplicado")
    void testCrearCategoria_NombreDuplicado() {
        // Arrange
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre("Electrónicos");
        nuevaCategoria.setDescripcion("Descripción duplicada");

        doNothing().when(categoriaValidator).validar(nuevaCategoria);
        when(categoriaRepository.existsByNombreIgnoreCase("Electrónicos")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.crearCategoria(nuevaCategoria))
                .isInstanceOf(CategoriaAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre: Electrónicos");

        verify(categoriaValidator, times(1)).validar(nuevaCategoria);
        verify(categoriaRepository, times(1)).existsByNombreIgnoreCase("Electrónicos");
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
    @DisplayName("Obtener categoría por nombre - Éxito")
    void testObtenerCategoriaPorNombre_Exitoso() {
        // Arrange
        when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("Electrónicos"))
                .thenReturn(Optional.of(categoriaTest));

        // Act
        Categoria resultado = categoriaService.obtenerCategoriaPorNombre("Electrónicos");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Electrónicos");

        verify(categoriaRepository, times(1)).findByNombreIgnoreCaseAndActivoTrue("Electrónicos");
    }

    @Test
    @DisplayName("Obtener categoría por nombre - No encontrada")
    void testObtenerCategoriaPorNombre_NoEncontrada() {
        // Arrange
        when(categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("Inexistente"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.obtenerCategoriaPorNombre("Inexistente"))
                .isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("No se encontró la categoría con nombre: Inexistente");

        verify(categoriaRepository, times(1)).findByNombreIgnoreCaseAndActivoTrue("Inexistente");
    }

    @Test
    @DisplayName("Listar categorías activas con paginación")
    void testListarCategoriasActivas_ConPaginacion() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
        Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

        when(categoriaRepository.findByActivoTrue(pageable)).thenReturn(page);

        // Act
        Page<Categoria> resultado = categoriaService.listarCategoriasActivas(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).contains(categoriaTest, categoriaTest2);

        verify(categoriaRepository, times(1)).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("Obtener categorías activas sin paginación")
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
    @DisplayName("Buscar categorías con filtros")
    void testBuscarCategorias_ConFiltros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Categoria> categorias = Arrays.asList(categoriaTest);
        Page<Categoria> page = new PageImpl<>(categorias, pageable, 1);

        // Mock estático para CategoriaSpecification
        try (var mockedStatic = mockStatic(CategoriaSpecification.class)) {
            Specification<Categoria> mockSpec = mock(Specification.class);
            mockedStatic.when(() -> CategoriaSpecification.conFiltros(filtroTest)).thenReturn(mockSpec);
            when(categoriaRepository.findAll(mockSpec, pageable)).thenReturn(page);

            // Act
            Page<Categoria> resultado = categoriaService.buscarCategorias(filtroTest, pageable);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0).getNombre()).isEqualTo("Electrónicos");

            verify(categoriaRepository, times(1)).findAll(mockSpec, pageable);
        }
    }

    @Test
    @DisplayName("Buscar categorías sin filtros")
    void testBuscarCategorias_SinFiltros() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Categoria> categorias = Arrays.asList(categoriaTest, categoriaTest2);
        Page<Categoria> page = new PageImpl<>(categorias, pageable, 2);

        when(categoriaRepository.findByActivoTrue(pageable)).thenReturn(page);

        // Act
        Page<Categoria> resultado = categoriaService.buscarCategorias(null, pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);

        verify(categoriaRepository, times(1)).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("Actualizar categoría - Éxito")
    void testActualizarCategoria_Exitoso() {
        // Arrange
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setNombre("Electrónicos Actualizados");
        categoriaActualizada.setDescripcion("Nueva descripción");
        categoriaActualizada.setActivo(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
        doNothing().when(categoriaValidator).validar(categoriaActualizada);
        when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Electrónicos Actualizados", 1L))
                .thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaTest);

        // Act
        Categoria resultado = categoriaService.actualizarCategoria(1L, categoriaActualizada);

        // Assert
        assertThat(resultado).isNotNull();

        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaValidator, times(1)).validar(categoriaActualizada);
        verify(categoriaRepository, times(1)).existsByNombreIgnoreCaseAndIdNot("Electrónicos Actualizados", 1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Actualizar categoría - Categoría no encontrada")
    void testActualizarCategoria_NoEncontrada() {
        // Arrange
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setNombre("Nuevo Nombre");

        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.actualizarCategoria(999L, categoriaActualizada))
                .isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("No se encontró la categoría con ID: 999");

        verify(categoriaRepository, times(1)).findById(999L);
        verify(categoriaValidator, never()).validar(any());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar categoría - Nombre duplicado")
    void testActualizarCategoria_NombreDuplicado() {
        // Arrange
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setNombre("Hogar");
        categoriaActualizada.setDescripcion("Nueva descripción");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
        doNothing().when(categoriaValidator).validar(categoriaActualizada);
        when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Hogar", 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.actualizarCategoria(1L, categoriaActualizada))
                .isInstanceOf(CategoriaAlreadyExistsException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre: Hogar");

        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaValidator, times(1)).validar(categoriaActualizada);
        verify(categoriaRepository, times(1)).existsByNombreIgnoreCaseAndIdNot("Hogar", 1L);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar categoría - Éxito")
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
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Eliminar categoría - No encontrada")
    void testEliminarCategoria_NoEncontrada() {
        // Arrange
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.eliminarCategoria(999L))
                .isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("No se encontró la categoría con ID: 999");

        verify(categoriaRepository, times(1)).findById(999L);
        verify(categoriaRepository, never()).tieneProductosActivos(any());
        verify(categoriaValidator, never()).validarEliminacion(any(), anyBoolean());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cambiar estado categoría - Activar")
    void testCambiarEstadoCategoria_Activar() {
        // Arrange
        categoriaTest.setActivo(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTest));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaTest);

        // Act
        Categoria resultado = categoriaService.cambiarEstadoCategoria(1L, true);

        // Assert
        assertThat(resultado).isNotNull();

        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
        verify(categoriaRepository, never()).tieneProductosActivos(any());
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
                .hasMessageContaining("No se puede desactivar la categoría")
                .hasMessageContaining("porque tiene productos activos asociados");

        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).tieneProductosActivos(1L);
        verify(categoriaRepository, never()).save(any());
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
    @DisplayName("Obtener categorías con cantidad de productos")
    void testObtenerCategoriasConCantidadProductos() {
        // Arrange
        Object[] categoria1 = {"Electrónicos", 5L};
        Object[] categoria2 = {"Hogar", 3L};
        List<Object[]> datos = Arrays.asList(categoria1, categoria2);

        when(categoriaRepository.findCategoriasConCantidadProductos()).thenReturn(datos);

        // Act
        List<Object[]> resultado = categoriaService.obtenerCategoriasConCantidadProductos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0)[0]).isEqualTo("Electrónicos");
        assertThat(resultado.get(0)[1]).isEqualTo(5L);

        verify(categoriaRepository, times(1)).findCategoriasConCantidadProductos();
    }

    @Test
    @DisplayName("Obtener categorías sin productos")
    void testObtenerCategoriasSinProductos() {
        // Arrange
        List<Categoria> categorias = Arrays.asList(categoriaTest2);
        when(categoriaRepository.findCategoriasSinProductos()).thenReturn(categorias);

        // Act
        List<Categoria> resultado = categoriaService.obtenerCategoriasSinProductos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Hogar");

        verify(categoriaRepository, times(1)).findCategoriasSinProductos();
    }

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
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);

        verify(categoriaRepository, times(1)).findCategoriasMasUtilizadas(pageable);
    }

    @Test
    @DisplayName("Verificar si existe categoría por nombre")
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
    @DisplayName("Buscar por nombre con paginación")
    void testBuscarPorNombre() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Categoria> categorias = Arrays.asList(categoriaTest);
        Page<Categoria> page = new PageImpl<>(categorias, pageable, 1);

        when(categoriaRepository.findByNombreContainingIgnoreCaseAndActivoTrue("Electr", pageable))
                .thenReturn(page);

        // Act
        Page<Categoria> resultado = categoriaService.buscarPorNombre("Electr", pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNombre()).contains("Electr");

        verify(categoriaRepository, times(1))
                .findByNombreContainingIgnoreCaseAndActivoTrue("Electr", pageable);
    }
}