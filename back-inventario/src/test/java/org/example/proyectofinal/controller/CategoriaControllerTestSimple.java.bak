package org.example.proyectofinal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.proyectofinal.dto.CategoriaCreateDTO;
import org.example.proyectofinal.dto.CategoriaDTO;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.exception.CategoriaAlreadyExistsException;
import org.example.proyectofinal.exception.CategoriaNotFoundException;
import org.example.proyectofinal.mapper.CategoriaMapper;
import org.example.proyectofinal.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * Pruebas unitarias simplificadas para CategoriaController.
 */
@WebMvcTest(CategoriaController.class)
class CategoriaControllerTestSimple {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaMapper categoriaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoriaTest;
    private CategoriaDTO categoriaDTO;
    private CategoriaCreateDTO categoriaCreateDTO;

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
                .build();

        categoriaCreateDTO = CategoriaCreateDTO.builder()
                .nombre("Electrónicos")
                .descripcion("Productos electrónicos y tecnológicos")
                .activo(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearCategoria_Exitoso() throws Exception {
        // Arrange
        when(categoriaMapper.toEntity(categoriaCreateDTO)).thenReturn(categoriaTest);
        when(categoriaService.crearCategoria(categoriaTest)).thenReturn(categoriaTest);
        when(categoriaMapper.toDTO(categoriaTest)).thenReturn(categoriaDTO);

        // Act & Assert
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaCreateDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónicos"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(print());

        verify(categoriaMapper, times(1)).toEntity(categoriaCreateDTO);
        verify(categoriaService, times(1)).crearCategoria(categoriaTest);
        verify(categoriaMapper, times(1)).toDTO(categoriaTest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearCategoria_NombreDuplicado() throws Exception {
        // Arrange
        when(categoriaMapper.toEntity(categoriaCreateDTO)).thenReturn(categoriaTest);
        when(categoriaService.crearCategoria(categoriaTest))
                .thenThrow(new CategoriaAlreadyExistsException("Categoría ya existe"));

        // Act & Assert
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaCreateDTO))
                        .with(csrf()))
                .andExpected(status().isConflict())
                .andExpect(jsonPath("$.error").value("Categoría ya existe"))
                .andDo(print());

        verify(categoriaMapper, times(1)).toEntity(categoriaCreateDTO);
        verify(categoriaService, times(1)).crearCategoria(categoriaTest);
        verify(categoriaMapper, never()).toDTO(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testObtenerCategoriaPorId_Exitoso() throws Exception {
        // Arrange
        when(categoriaService.obtenerCategoriaPorId(1L)).thenReturn(categoriaTest);
        when(categoriaMapper.toDTO(categoriaTest)).thenReturn(categoriaDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónicos"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(print());

        verify(categoriaService, times(1)).obtenerCategoriaPorId(1L);
        verify(categoriaMapper, times(1)).toDTO(categoriaTest);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testObtenerCategoriaPorId_NoEncontrada() throws Exception {
        // Arrange
        when(categoriaService.obtenerCategoriaPorId(999L))
                .thenThrow(new CategoriaNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/categorias/999"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(categoriaService, times(1)).obtenerCategoriaPorId(999L);
        verify(categoriaMapper, never()).toDTO(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarCategoria_Exitoso() throws Exception {
        // Arrange
        doNothing().when(categoriaService).eliminarCategoria(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categorias/1")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(categoriaService, times(1)).eliminarCategoria(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testListarCategoriasActivas() throws Exception {
        // Arrange
        List<Categoria> categorias = Arrays.asList(categoriaTest);
        List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaService.listarCategoriasActivas()).thenReturn(categorias);
        when(categoriaMapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Electrónicos"))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andDo(print());

        verify(categoriaService, times(1)).listarCategoriasActivas();
        verify(categoriaMapper, times(1)).toDTOList(categorias);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testListarCategoriasConPaginacion() throws Exception {
        // Arrange
        List<Categoria> categorias = Arrays.asList(categoriaTest);
        Page<Categoria> page = new PageImpl<>(categorias, PageRequest.of(0, 10), 1);
        List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaService.listarCategoriasConPaginacion(any())).thenReturn(page);
        when(categoriaMapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categorias/paginado")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Electrónicos"))
                .andDo(print());

        verify(categoriaService, times(1)).listarCategoriasConPaginacion(any());
        verify(categoriaMapper, times(1)).toDTOList(categorias);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearCategoria_DatosInvalidos() throws Exception {
        // Arrange
        CategoriaCreateDTO categoriaInvalida = CategoriaCreateDTO.builder()
                .nombre("") // Nombre vacío
                .descripcion("A".repeat(1500)) // Descripción muy larga
                .activo(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInvalida))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andDo(print());

        verify(categoriaService, never()).crearCategoria(any());
    }

    @Test
    void testAccesoSinAutenticacion() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccesoNoAutorizado() throws Exception {
        // Act & Assert - Usuario sin rol ADMIN intenta crear categoría
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaCreateDTO))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testObtenerCategoriaConIdInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/categorias/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
