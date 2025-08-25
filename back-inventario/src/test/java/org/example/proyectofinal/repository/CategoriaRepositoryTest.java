package org.example.proyectofinal.repository;

import org.example.proyectofinal.entity.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas de integración para CategoriaRepository.
 * Utiliza @DataJpaTest para configurar solo la capa de persistencia.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoriaRepository - Pruebas de Integración")
class CategoriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaElectronicos;
    private Categoria categoriaHogar;
    private Categoria categoriaInactiva;

    @BeforeEach
    void setUp() {
        categoriaElectronicos = Categoria.builder()
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        categoriaHogar = Categoria.builder()
                .nombre("Hogar")
                .descripcion("Productos para el hogar")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        categoriaInactiva = Categoria.builder()
                .nombre("Deportes")
                .descripcion("Artículos deportivos")
                .activo(false)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        // Persistir las categorías en la base de datos de pruebas
        entityManager.persistAndFlush(categoriaElectronicos);
        entityManager.persistAndFlush(categoriaHogar);
        entityManager.persistAndFlush(categoriaInactiva);
    }

    @Test
    @DisplayName("Buscar categorías activas ordenadas por nombre")
    void testFindByActivoTrueOrderByNombre() {
        // Act
        List<Categoria> resultado = categoriaRepository.findByActivoTrueOrderByNombre();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Electrónicos");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Hogar");
        assertThat(resultado).allMatch(Categoria::isActivo);
    }

    @Test
    @DisplayName("Buscar categorías activas con paginación")
    void testFindByActivoTrue() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        var resultado = categoriaRepository.findByActivoTrue(pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent().get(0).isActivo()).isTrue();
    }

    @Test
    @DisplayName("Buscar categoría por nombre (case insensitive)")
    void testFindByNombreIgnoreCaseAndActivoTrue() {
        // Act
        Optional<Categoria> resultado1 = categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("electrónicos");
        Optional<Categoria> resultado2 = categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("HOGAR");
        Optional<Categoria> resultado3 = categoriaRepository.findByNombreIgnoreCaseAndActivoTrue("deportes");

        // Assert
        assertThat(resultado1).isPresent();
        assertThat(resultado1.get().getNombre()).isEqualTo("Electrónicos");

        assertThat(resultado2).isPresent();
        assertThat(resultado2.get().getNombre()).isEqualTo("Hogar");

        // Deportes está inactiva, no debería encontrarse
        assertThat(resultado3).isEmpty();
    }

    @Test
    @DisplayName("Verificar existencia por nombre (case insensitive)")
    void testExistsByNombreIgnoreCaseAndActivoTrue() {
        // Act & Assert
        assertThat(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("ELECTRÓNICOS")).isTrue();
        assertThat(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("hogar")).isTrue();
        assertThat(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("deportes")).isFalse(); // Inactiva
        assertThat(categoriaRepository.existsByNombreIgnoreCaseAndActivoTrue("inexistente")).isFalse();
    }

    @Test
    @DisplayName("Contar categorías activas")
    void testCountByActivoTrue() {
        // Act
        Long count = categoriaRepository.countByActivoTrue();

        // Assert
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Buscar categorías que contengan texto en nombre")
    void testFindByNombreContainingIgnoreCaseAndActivoTrue() {
        // Act
        List<Categoria> resultado = categoriaRepository.findByNombreContainingIgnoreCaseAndActivoTrue("elect");

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Electrónicos");
    }

    @Test
    @DisplayName("Buscar categorías que contengan texto en descripción")
    void testFindByDescripcionContainingIgnoreCaseAndActivoTrue() {
        // Act
        List<Categoria> resultado = categoriaRepository.findByDescripcionContainingIgnoreCaseAndActivoTrue("productos");

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Categoria::getNombre)
                .containsExactlyInAnyOrder("Electrónicos", "Hogar");
    }

    @Test
    @DisplayName("Auditoría - Verificar fechas de creación")
    void testAuditoria() {
        // Act
        Optional<Categoria> categoria = categoriaRepository.findById(categoriaElectronicos.getId());

        // Assert
        assertThat(categoria).isPresent();
        assertThat(categoria.get().getFechaCreacion()).isNotNull();
        assertThat(categoria.get().getFechaCreacion()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("Persistencia - Guardar nueva categoría")
    void testGuardarNuevaCategoria() {
        // Arrange
        Categoria nuevaCategoria = Categoria.builder()
                .nombre("Gaming")
                .descripcion("Productos para videojuegos")
                .activo(true)
                .build();

        // Act
        Categoria categoriaGuardada = categoriaRepository.save(nuevaCategoria);

        // Assert
        assertThat(categoriaGuardada.getId()).isNotNull();
        assertThat(categoriaGuardada.getNombre()).isEqualTo("Gaming");
        assertThat(categoriaGuardada.getFechaCreacion()).isNotNull();

        // Verificar en base de datos
        Optional<Categoria> categoriaEnBD = categoriaRepository.findById(categoriaGuardada.getId());
        assertThat(categoriaEnBD).isPresent();
        assertThat(categoriaEnBD.get().getNombre()).isEqualTo("Gaming");
    }

    @Test
    @DisplayName("Actualización - Modificar categoría existente")
    void testActualizarCategoria() {
        // Arrange
        String nuevaDescripcion = "Nueva descripción de electrónicos";

        // Act
        categoriaElectronicos.setDescripcion(nuevaDescripcion);
        Categoria categoriaActualizada = categoriaRepository.save(categoriaElectronicos);

        // Assert
        assertThat(categoriaActualizada.getDescripcion()).isEqualTo(nuevaDescripcion);
        assertThat(categoriaActualizada.getFechaModificacion()).isNotNull();

        // Verificar en base de datos
        Optional<Categoria> categoriaEnBD = categoriaRepository.findById(categoriaElectronicos.getId());
        assertThat(categoriaEnBD).isPresent();
        assertThat(categoriaEnBD.get().getDescripcion()).isEqualTo(nuevaDescripcion);
    }

    @Test
    @DisplayName("Soft Delete - Desactivar categoría")
    void testSoftDelete() {
        // Arrange
        Long idCategoria = categoriaElectronicos.getId();

        // Act
        categoriaElectronicos.setActivo(false);
        categoriaRepository.save(categoriaElectronicos);

        // Assert
        List<Categoria> categoriasActivas = categoriaRepository.findByActivoTrueOrderByNombre();
        assertThat(categoriasActivas).hasSize(1);
        assertThat(categoriasActivas.get(0).getNombre()).isEqualTo("Hogar");

        // La categoría sigue existiendo en BD pero inactiva
        Optional<Categoria> categoriaInactivaEnBD = categoriaRepository.findById(idCategoria);
        assertThat(categoriaInactivaEnBD).isPresent();
        assertThat(categoriaInactivaEnBD.get().isActivo()).isFalse();
    }

    @Test
    @DisplayName("Constraint de unicidad - Nombre único")
    void testConstraintNombreUnico() {
        // Arrange
        Categoria categoriaDuplicada = Categoria.builder()
                .nombre("Electrónicos") // Mismo nombre que una categoría existente
                .descripcion("Otra descripción")
                .activo(true)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> {
            categoriaRepository.saveAndFlush(categoriaDuplicada);
        }).isInstanceOf(Exception.class); // DataIntegrityViolationException o similar
    }

    @Test
    @DisplayName("Búsqueda con criterios múltiples")
    void testBusquedaConCriteriosMultiples() {
        // Crear más categorías para pruebas
        Categoria categoria1 = Categoria.builder()
                .nombre("Tecnología")
                .descripcion("Productos tecnológicos avanzados")
                .activo(true)
                .build();

        Categoria categoria2 = Categoria.builder()
                .nombre("Mobiliario")
                .descripcion("Muebles para el hogar")
                .activo(true)
                .build();

        entityManager.persistAndFlush(categoria1);
        entityManager.persistAndFlush(categoria2);

        // Act - Buscar categorías que contengan "tecno" en nombre o descripción
        List<Categoria> porNombre = categoriaRepository.findByNombreContainingIgnoreCaseAndActivoTrue("tecno");
        List<Categoria> porDescripcion = categoriaRepository.findByDescripcionContainingIgnoreCaseAndActivoTrue("tecnológicos");

        // Assert
        assertThat(porNombre).hasSize(1);
        assertThat(porNombre.get(0).getNombre()).isEqualTo("Tecnología");

        assertThat(porDescripcion).hasSize(1);
        assertThat(porDescripcion.get(0).getDescripcion()).contains("tecnológicos");
    }
}
