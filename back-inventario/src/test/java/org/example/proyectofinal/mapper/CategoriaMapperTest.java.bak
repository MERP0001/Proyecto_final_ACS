package org.example.proyectofinal.mapper;

import org.example.proyectofinal.dto.CategoriaDTO;
import org.example.proyectofinal.entity.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para CategoriaMapper.
 * Valida la correcta conversión entre entidades y DTOs.
 */
class CategoriaMapperTest {

    private CategoriaMapper mapper;
    private Categoria categoria;
    private CategoriaDTO categoriaDTO;
    private LocalDateTime fechaPrueba;

    @BeforeEach
    void setUp() {
        mapper = CategoriaMapper.INSTANCE;
        fechaPrueba = LocalDateTime.of(2023, 12, 15, 10, 30, 0);

        categoria = Categoria.builder()
                .id(1L)
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .fechaModificacion(fechaPrueba.plusHours(1))
                .version(0L)
                .build();

        categoriaDTO = CategoriaDTO.builder()
                .id(1L)
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .fechaModificacion(fechaPrueba.plusHours(1))
                .version(0L)
                .build();
    }

    @Test
    void testToDTOCompleto() {
        // Act
        CategoriaDTO resultado = mapper.toDTO(categoria);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(categoria.getId());
        assertThat(resultado.getNombre()).isEqualTo(categoria.getNombre());
        assertThat(resultado.getDescripcion()).isEqualTo(categoria.getDescripcion());
        assertThat(resultado.isActivo()).isEqualTo(categoria.isActivo());
        assertThat(resultado.getFechaCreacion()).isEqualTo(categoria.getFechaCreacion());
        assertThat(resultado.getFechaModificacion()).isEqualTo(categoria.getFechaModificacion());
        assertThat(resultado.getVersion()).isEqualTo(categoria.getVersion());
    }

    @Test
    void testToDTOConEntidadNula() {
        // Act
        CategoriaDTO resultado = mapper.toDTO(null);

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    void testToDTOConCamposNulos() {
        // Arrange
        Categoria categoriaConNulos = Categoria.builder()
                .id(1L)
                .nombre("Categoría Simple")
                .descripcion(null)
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .fechaModificacion(null)
                .version(0L)
                .build();

        // Act
        CategoriaDTO resultado = mapper.toDTO(categoriaConNulos);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Categoría Simple");
        assertThat(resultado.getDescripcion()).isNull();
        assertThat(resultado.isActivo()).isTrue();
        assertThat(resultado.getFechaCreacion()).isEqualTo(fechaPrueba);
        assertThat(resultado.getFechaModificacion()).isNull();
        assertThat(resultado.getVersion()).isEqualTo(0L);
    }

    @Test
    void testToDTOList() {
        // Arrange
        Categoria categoria2 = Categoria.builder()
                .id(2L)
                .nombre("Hogar")
                .descripcion("Productos para el hogar")
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .version(0L)
                .build();

        List<Categoria> categorias = Arrays.asList(categoria, categoria2);

        // Act
        List<CategoriaDTO> resultado = mapper.toDTOList(categorias);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);

        CategoriaDTO dto1 = resultado.get(0);
        assertThat(dto1.getId()).isEqualTo(1L);
        assertThat(dto1.getNombre()).isEqualTo("Electrónicos");

        CategoriaDTO dto2 = resultado.get(1);
        assertThat(dto2.getId()).isEqualTo(2L);
        assertThat(dto2.getNombre()).isEqualTo("Hogar");
    }

    @Test
    void testToDTOListConListaNula() {
        // Act
        List<CategoriaDTO> resultado = mapper.toDTOList(null);

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    void testToEntityCompleto() {
        // Act
        Categoria resultado = mapper.toEntity(categoriaDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(categoriaDTO.getId());
        assertThat(resultado.getNombre()).isEqualTo(categoriaDTO.getNombre());
        assertThat(resultado.getDescripcion()).isEqualTo(categoriaDTO.getDescripcion());
        assertThat(resultado.isActivo()).isEqualTo(categoriaDTO.isActivo());
        assertThat(resultado.getFechaCreacion()).isEqualTo(categoriaDTO.getFechaCreacion());
        assertThat(resultado.getFechaModificacion()).isEqualTo(categoriaDTO.getFechaModificacion());
        assertThat(resultado.getVersion()).isEqualTo(categoriaDTO.getVersion());
    }

    @Test
    void testToEntityConDTONulo() {
        // Act
        Categoria resultado = mapper.toEntity(null);

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    void testToEntityParaCreacion() {
        // Arrange
        CategoriaDTO dtoParaCreacion = CategoriaDTO.builder()
                .nombre("Nueva Categoría")
                .descripcion("Descripción de nueva categoría")
                .activo(true)
                .build();

        // Act
        Categoria resultado = mapper.toEntity(dtoParaCreacion);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNull();
        assertThat(resultado.getNombre()).isEqualTo("Nueva Categoría");
        assertThat(resultado.getDescripcion()).isEqualTo("Descripción de nueva categoría");
        assertThat(resultado.isActivo()).isTrue();
        assertThat(resultado.getFechaCreacion()).isNull();
        assertThat(resultado.getFechaModificacion()).isNull();
        assertThat(resultado.getVersion()).isNull();
    }

    @Test
    void testToEntityConCamposNulos() {
        // Arrange
        CategoriaDTO dtoConNulos = CategoriaDTO.builder()
                .id(1L)
                .nombre("Categoría Mínima")
                .descripcion(null)
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .fechaModificacion(null)
                .version(0L)
                .build();

        // Act
        Categoria resultado = mapper.toEntity(dtoConNulos);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Categoría Mínima");
        assertThat(resultado.getDescripcion()).isNull();
        assertThat(resultado.isActivo()).isTrue();
        assertThat(resultado.getFechaCreacion()).isEqualTo(fechaPrueba);
        assertThat(resultado.getFechaModificacion()).isNull();
        assertThat(resultado.getVersion()).isEqualTo(0L);
    }

    @Test
    void testUpdateEntityFromDTO() {
        // Arrange
        Categoria entidadExistente = Categoria.builder()
                .id(1L)
                .nombre("Nombre Original")
                .descripcion("Descripción Original")
                .activo(false)
                .fechaCreacion(fechaPrueba.minusDays(1))
                .fechaModificacion(fechaPrueba.minusHours(2))
                .version(1L)
                .build();

        CategoriaDTO dtoActualizacion = CategoriaDTO.builder()
                .id(1L)
                .nombre("Nombre Actualizado")
                .descripcion("Descripción Actualizada")
                .activo(true)
                .version(1L)
                .build();

        // Act
        mapper.updateEntityFromDTO(dtoActualizacion, entidadExistente);

        // Assert
        assertThat(entidadExistente.getId()).isEqualTo(1L);
        assertThat(entidadExistente.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(entidadExistente.getDescripcion()).isEqualTo("Descripción Actualizada");
        assertThat(entidadExistente.isActivo()).isTrue();
        assertThat(entidadExistente.getFechaCreacion()).isEqualTo(fechaPrueba.minusDays(1));
        assertThat(entidadExistente.getVersion()).isEqualTo(1L);
    }

    @Test
    void testConversionBidireccional() {
        // Act
        CategoriaDTO dto = mapper.toDTO(categoria);
        Categoria entidadRecuperada = mapper.toEntity(dto);

        // Assert
        assertThat(entidadRecuperada.getId()).isEqualTo(categoria.getId());
        assertThat(entidadRecuperada.getNombre()).isEqualTo(categoria.getNombre());
        assertThat(entidadRecuperada.getDescripcion()).isEqualTo(categoria.getDescripcion());
        assertThat(entidadRecuperada.isActivo()).isEqualTo(categoria.isActivo());
        assertThat(entidadRecuperada.getFechaCreacion()).isEqualTo(categoria.getFechaCreacion());
        assertThat(entidadRecuperada.getFechaModificacion()).isEqualTo(categoria.getFechaModificacion());
        assertThat(entidadRecuperada.getVersion()).isEqualTo(categoria.getVersion());
    }

    @Test
    void testMapearCategoriaConIdGrande() {
        // Arrange
        Categoria categoriaIdGrande = Categoria.builder()
                .id(Long.MAX_VALUE)
                .nombre("Categoría ID Máximo")
                .descripcion("Prueba con ID máximo")
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .version(0L)
                .build();

        // Act
        CategoriaDTO resultado = mapper.toDTO(categoriaIdGrande);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(Long.MAX_VALUE);
        assertThat(resultado.getNombre()).isEqualTo("Categoría ID Máximo");
    }

    @Test
    void testMapearCategoriaConStringsLargos() {
        // Arrange
        String nombreLargo = "A".repeat(255);
        String descripcionLarga = "B".repeat(1000);

        Categoria categoriaStringsLargos = Categoria.builder()
                .id(1L)
                .nombre(nombreLargo)
                .descripcion(descripcionLarga)
                .activo(true)
                .fechaCreacion(fechaPrueba)
                .version(0L)
                .build();

        // Act
        CategoriaDTO resultado = mapper.toDTO(categoriaStringsLargos);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo(nombreLargo);
        assertThat(resultado.getDescripcion()).isEqualTo(descripcionLarga);
    }

    @Test
    void testMapearCategoriaConFechasExtremas() {
        // Arrange
        LocalDateTime fechaMinima = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        LocalDateTime fechaMaxima = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        Categoria categoriaFechasExtremas = Categoria.builder()
                .id(1L)
                .nombre("Categoría Fechas Extremas")
                .descripcion("Prueba con fechas límite")
                .activo(true)
                .fechaCreacion(fechaMinima)
                .fechaModificacion(fechaMaxima)
                .version(0L)
                .build();

        // Act
        CategoriaDTO resultado = mapper.toDTO(categoriaFechasExtremas);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getFechaCreacion()).isEqualTo(fechaMinima);
        assertThat(resultado.getFechaModificacion()).isEqualTo(fechaMaxima);
    }
}
