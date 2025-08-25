package org.example.proyectofinal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.proyectofinal.dto.CategoriaCreateDTO;
import org.example.proyectofinal.dto.CategoriaDTO;
import org.example.proyectofinal.dto.CategoriaUpdateDTO;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.filter.CategoriaFilter;
import org.example.proyectofinal.mapper.CategoriaMapper;
import org.example.proyectofinal.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para CategoriaController.
 * Utiliza @WebMvcTest para probar solo la capa web.
 */
@WebMvcTest(CategoriaController.class)
@DisplayName("CategoriaController - Pruebas Unitarias")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaMapper categoriaMapper;

    private Categoria categoriaTest;
    private CategoriaDTO categoriaDTO;
    private CategoriaCreateDTO categoriaCreateDTO;
    private CategoriaUpdateDTO categoriaUpdateDTO;

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

        categoriaDTO = CategoriaDTO.builder()
                .id(1L)
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .cantidadProductos(10L)
                .build();

        categoriaCreateDTO = CategoriaCreateDTO.builder()
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .build();

        categoriaUpdateDTO = CategoriaUpdateDTO.builder()
                .nombre("Electrónicos Actualizados")
                .descripcion("Nueva descripción")
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Operaciones CRUD")
    class OperacionesCRUD {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Crear categoría - Éxito")
        void testCrearCategoria_Exitoso() throws Exception {
            // Arrange
            when(categoriaMapper.toEntity(categoriaCreateDTO)).thenReturn(categoriaTest);
            when(categoriaService.crearCategoria(categoriaTest)).thenReturn(categoriaTest);
            when(categoriaMapper.toDTO(categoriaTest)).thenReturn(categoriaDTO);

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaCreateDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Electrónicos"));

            verify(categoriaMapper, times(1)).toEntity(categoriaCreateDTO);
            verify(categoriaService, times(1)).crearCategoria(categoriaTest);
            verify(categoriaMapper, times(1)).toDTO(categoriaTest);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Crear categoría - Nombre duplicado")
        void testCrearCategoria_NombreDuplicado() throws Exception {
            // Arrange
            when(categoriaMapper.toEntity(categoriaCreateDTO)).thenReturn(categoriaTest);
            when(categoriaService.crearCategoria(categoriaTest))
                    .thenThrow(new CategoriaAlreadyExistsException("Categoría ya existe"));

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaCreateDTO)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Categoría ya existe"));

            verify(categoriaMapper, times(1)).toEntity(categoriaCreateDTO);
            verify(categoriaService, times(1)).crearCategoria(categoriaTest);
            verify(categoriaMapper, never()).toDTO(any());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Crear categoría - Sin permisos")
        void testCrearCategoria_SinPermisos() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaCreateDTO)))
                    .andDo(print())
                    .andExpect(status().isForbidden());

            verify(categoriaService, never()).crearCategoria(any());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Obtener categoría por ID - Éxito")
        void testObtenerCategoriaPorId_Exitoso() throws Exception {
            // Arrange
            when(categoriaService.obtenerCategoriaPorId(1L)).thenReturn(categoriaTest);
            when(categoriaMapper.toDTO(categoriaTest)).thenReturn(categoriaDTO);

            // Act & Assert
            mockMvc.perform(get("/api/categorias/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Electrónicos"));

            verify(categoriaService, times(1)).obtenerCategoriaPorId(1L);
            verify(categoriaMapper, times(1)).toDTO(categoriaTest);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Obtener categoría por ID - No encontrada")
        void testObtenerCategoriaPorId_NoEncontrada() throws Exception {
            // Arrange
            when(categoriaService.obtenerCategoriaPorId(999L))
                    .thenThrow(new CategoriaNotFoundException("Categoría con ID 999 no encontrada"));

            // Act & Assert
            mockMvc.perform(get("/api/categorias/999")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Categoría con ID 999 no encontrada"));

            verify(categoriaService, times(1)).obtenerCategoriaPorId(999L);
            verify(categoriaMapper, never()).toDTO(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Actualizar categoría - Éxito")
        void testActualizarCategoria_Exitoso() throws Exception {
            // Arrange
            when(categoriaMapper.toEntity(categoriaUpdateDTO)).thenReturn(categoriaTest);
            when(categoriaService.actualizarCategoria(1L, categoriaTest)).thenReturn(categoriaTest);
            when(categoriaMapper.toDTO(categoriaTest)).thenReturn(categoriaDTO);

            // Act & Assert
            mockMvc.perform(put("/api/categorias/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaUpdateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Electrónicos"));

            verify(categoriaMapper, times(1)).toEntity(categoriaUpdateDTO);
            verify(categoriaService, times(1)).actualizarCategoria(1L, categoriaTest);
            verify(categoriaMapper, times(1)).toDTO(categoriaTest);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Eliminar categoría - Éxito")
        void testEliminarCategoria_Exitoso() throws Exception {
            // Arrange
            doNothing().when(categoriaService).eliminarCategoria(1L);

            // Act & Assert
            mockMvc.perform(delete("/api/categorias/1")
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(categoriaService, times(1)).eliminarCategoria(1L);
        }
    }

    @Nested
    @DisplayName("Operaciones de Consulta")
    class OperacionesConsulta {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Listar categorías - Con paginación")
        void testListarCategorias_ConPaginacion() throws Exception {
            // Arrange
            List<Categoria> categorias = Arrays.asList(categoriaTest);
            List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);
            Page<Categoria> page = new PageImpl<>(categorias, PageRequest.of(0, 10), 1);

            when(categoriaService.listarCategoriasConPaginacion(any(Pageable.class))).thenReturn(page);
            when(categoriaMapper.toDTOList(categorias)).thenReturn(categoriasDTO);

            // Act & Assert
            mockMvc.perform(get("/api/categorias")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpected(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.number").value(0));

            verify(categoriaService, times(1)).listarCategoriasConPaginacion(any(Pageable.class));
            verify(categoriaMapper, times(1)).toDTOList(categorias);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Buscar categorías con filtros")
        void testBuscarCategoriasConFiltros() throws Exception {
            // Arrange
            List<Categoria> categorias = Arrays.asList(categoriaTest);
            List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);
            Page<Categoria> page = new PageImpl<>(categorias, PageRequest.of(0, 10), 1);

            when(categoriaService.buscarCategoriasConFiltros(any(CategoriaFilter.class), any(Pageable.class)))
                    .thenReturn(page);
            when(categoriaMapper.toDTOList(categorias)).thenReturn(categoriasDTO);

            // Act & Assert
            mockMvc.perform(get("/api/categorias/buscar")
                    .param("nombre", "Elect")
                    .param("activo", "true")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].nombre").value("Electrónicos"));

            verify(categoriaService, times(1)).buscarCategoriasConFiltros(any(CategoriaFilter.class), any(Pageable.class));
            verify(categoriaMapper, times(1)).toDTOList(categorias);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Listar categorías activas")
        void testListarCategoriasActivas() throws Exception {
            // Arrange
            List<Categoria> categorias = Arrays.asList(categoriaTest);
            List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);

            when(categoriaService.listarCategoriasActivas()).thenReturn(categorias);
            when(categoriaMapper.toDTOList(categorias)).thenReturn(categoriasDTO);

            // Act & Assert
            mockMvc.perform(get("/api/categorias/activas")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].activo").value(true));

            verify(categoriaService, times(1)).listarCategoriasActivas();
            verify(categoriaMapper, times(1)).toDTOList(categorias);
        }
    }

    @Nested
    @DisplayName("Validaciones de Entrada")
    class ValidacionesEntrada {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Crear categoría - Nombre vacío")
        void testCrearCategoria_NombreVacio() throws Exception {
            // Arrange
            CategoriaCreateDTO categoriaInvalida = CategoriaCreateDTO.builder()
                    .nombre("")
                    .descripcion("Descripción válida")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaInvalida)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());

            verify(categoriaService, never()).crearCategoria(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Crear categoría - Nombre demasiado largo")
        void testCrearCategoria_NombreMuyLargo() throws Exception {
            // Arrange
            String nombreMuyLargo = "A".repeat(51); // Excede el límite de 50 caracteres
            CategoriaCreateDTO categoriaInvalida = CategoriaCreateDTO.builder()
                    .nombre(nombreMuyLargo)
                    .descripcion("Descripción válida")
                    .build();

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaInvalida)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());

            verify(categoriaService, never()).crearCategoria(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Crear categoría - Descripción demasiado larga")
        void testCrearCategoria_DescripcionMuyLarga() throws Exception {
            // Arrange
            String descripcionMuyLarga = "A".repeat(201); // Excede el límite de 200 caracteres
            CategoriaCreateDTO categoriaInvalida = CategoriaCreateDTO.builder()
                    .nombre("Nombre válido")
                    .descripcion(descripcionMuyLarga)
                    .build();

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoriaInvalida)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());

            verify(categoriaService, never()).crearCategoria(any());
        }
    }

    @Nested
    @DisplayName("Casos Edge")
    class CasosEdge {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Obtener categoría - ID inválido")
        void testObtenerCategoria_IdInvalido() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/categorias/abc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(categoriaService, never()).obtenerCategoriaPorId(any());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Listar categorías - Parámetros de paginación inválidos")
        void testListarCategorias_PaginacionInvalida() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/categorias")
                    .param("page", "-1")
                    .param("size", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(categoriaService, never()).listarCategoriasConPaginacion(any());
        }

        @Test
        @DisplayName("Acceso sin autenticación")
        void testAccesoSinAutenticacion() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/categorias/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(categoriaService, never()).obtenerCategoriaPorId(any());
        }
    }
}
