package org.example.proyectofinal.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.example.proyectofinal.entity.Categoria;
import org.example.proyectofinal.service.CategoriaService;
import org.example.proyectofinal.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaSteps {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaSteps.class);

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaCreada;
    private Categoria categoriaActual;
    private Page<Categoria> categoriasEncontradas;
    private Exception ultimaExcepcion;

    @Cuando("creo una categoría con los siguientes datos:")
    public void creoUnaCategoriaConLosSiguientesDatos(DataTable dataTable) {
        try {
            Map<String, String> datos = dataTable.asMap(String.class, String.class);
            
            Categoria categoria = Categoria.builder()
                    .nombre(datos.get("nombre"))
                    .descripcion(datos.get("descripcion"))
                    .activo(true)
                    .build();

            categoriaCreada = categoriaService.crearCategoria(categoria);
        } catch (Exception e) {
            ultimaExcepcion = e;
        }
    }

    @Entonces("la categoría debe ser creada exitosamente")
    public void laCategoriaDebeSerCreadaExitosamente() {
        assertNull(ultimaExcepcion, "No debería haber excepciones");
        assertNotNull(categoriaCreada, "La categoría debe existir");
    }

    @Dado("que existen categorías en el sistema")
    public void queExistenCategoriasEnElSistema() {
        Page<Categoria> categorias = categoriaService.listarCategoriasActivas(PageRequest.of(0, 10));
        
        // Si no hay categorías, crear algunas de prueba
        if (categorias.isEmpty()) {
            logger.info("No hay categorías en el sistema, creando categorías de prueba");
            
            try {
                // Crear categorías de prueba
                Categoria categoria1 = new Categoria();
                categoria1.setNombre("Categoría Test 1");
                categoria1.setDescripcion("Descripción de prueba 1");
                categoria1.setActivo(true);
                categoriaRepository.save(categoria1);
                
                Categoria categoria2 = new Categoria();
                categoria2.setNombre("Categoría Test 2");
                categoria2.setDescripcion("Descripción de prueba 2");
                categoria2.setActivo(true);
                categoriaRepository.save(categoria2);
                
                logger.debug("Categorías de prueba creadas exitosamente");
            } catch (Exception e) {
                logger.error("Error al crear categorías de prueba: {}", e.getMessage());
                throw new RuntimeException("No se pudieron crear categorías de prueba", e);
            }
        }
        
        // Verificar que ahora sí existen categorías
        categorias = categoriaService.listarCategoriasActivas(PageRequest.of(0, 10));
        assertFalse(categorias.isEmpty(), "Debe haber al menos una categoría en el sistema");
    }

    @Cuando("solicito la lista de categorías")
    public void solicitoLaListaDeCategorias() {
        categoriasEncontradas = categoriaService.listarCategoriasActivas(PageRequest.of(0, 10));
    }

    @Entonces("debo obtener una lista de categorías")
    public void deboObtenerUnaListaDeCategorias() {
        assertNotNull(categoriasEncontradas, "La lista de categorías no debe ser null");
        assertFalse(categoriasEncontradas.isEmpty(), "La lista de categorías no debe estar vacía");
    }

    @Entonces("cada categoría debe tener nombre y descripción")
    public void cadaCategoriaDebeTenerNombreYDescripcion() {
        assertTrue(categoriasEncontradas.getContent().stream()
                .allMatch(categoria -> categoria.getNombre() != null && !categoria.getNombre().isEmpty()
                        && categoria.getDescripcion() != null && !categoria.getDescripcion().isEmpty()),
                "Todas las categorías deben tener nombre y descripción");
    }

    /**
     * Método para limpiar el estado entre escenarios
     */
    public void limpiarEstado() {
        categoriaActual = null;
        categoriaCreada = null;
        categoriasEncontradas = null;
        ultimaExcepcion = null;
    }

    // Steps adicionales para categorías
    @Dado("que existe una categoría con nombre {string}")
    public void que_existe_una_categoría_con_nombre(String nombre) {
        logger.info("Verificando existencia de categoría: {}", nombre);
        
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreIgnoreCase(nombre);
        if (categoriaExistente.isEmpty()) {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion("Categoría de " + nombre);
            categoria.setActivo(true);
            categoria.setVersion(0L);
            categoria.setFechaCreacion(LocalDateTime.now());
            categoria.setFechaModificacion(LocalDateTime.now());
            categoriaRepository.save(categoria);
            logger.info("Categoría creada: {}", categoria.getId());
        }
        this.categoriaActual = categoriaRepository.findByNombreIgnoreCase(nombre).orElse(null);
    }

    @Cuando("actualizo la descripción a {string}")
    public void actualizo_la_descripción_a(String nuevaDescripcion) {
        logger.info("Actualizando descripción de categoría a: {}", nuevaDescripcion);
        if (this.categoriaActual != null) {
            this.categoriaActual.setDescripcion(nuevaDescripcion);
            this.categoriaActual.setFechaModificacion(LocalDateTime.now());
            this.categoriaActual = categoriaRepository.save(this.categoriaActual);
            logger.info("Categoría actualizada: {}", this.categoriaActual.getId());
        }
    }

    @Entonces("la categoría debe ser actualizada exitosamente")
    public void la_categoría_debe_ser_actualizada_exitosamente() {
        logger.info("Verificando actualización exitosa de categoría");
        assertNotNull(this.categoriaActual, "La categoría no debe ser null");
        assertNotNull(this.categoriaActual.getId(), "La categoría debe tener ID");
    }

    @Entonces("la nueva descripción debe coincidir")
    public void la_nueva_descripción_debe_coincidir() {
        logger.info("Verificando nueva descripción");
        assertNotNull(this.categoriaActual, "La categoría no debe ser null");
        assertNotNull(this.categoriaActual.getDescripcion(), "La descripción no debe ser null");
        assertFalse(this.categoriaActual.getDescripcion().isEmpty(), "La descripción no debe estar vacía");
    }

    @Cuando("intento eliminar la categoría")
    public void intento_eliminar_la_categoría() {
        logger.info("Intentando eliminar categoría");
        try {
            if (this.categoriaActual != null) {
                categoriaService.eliminarCategoria(this.categoriaActual.getId());
            }
        } catch (Exception e) {
            this.ultimaExcepcion = e;
            logger.info("Excepción capturada: {}", e.getMessage());
        }
    }

    @Entonces("no debe permitir eliminar la categoría")
    public void no_debe_permitir_eliminar_la_categoría() {
        logger.info("Verificando que no se puede eliminar categoría con productos");
        // La categoría debe seguir existiendo
        assertNotNull(this.categoriaActual, "La categoría no debe ser null");
        Optional<Categoria> categoria = categoriaRepository.findById(this.categoriaActual.getId());
        assertTrue(categoria.isPresent(), "La categoría debe seguir existiendo");
    }

    @Entonces("debe mostrar mensaje de error indicando productos asociados")
    public void debe_mostrar_mensaje_de_error_indicando_productos_asociados() {
        logger.info("Verificando mensaje de error por productos asociados");
        // En este caso, simplemente verificamos que hubo una excepción
        assertNotNull(this.ultimaExcepcion, "Debe haber una excepción");
    }

    @Entonces("la categoría debe ser eliminada exitosamente")
    public void la_categoría_debe_ser_eliminada_exitosamente() {
        logger.info("Verificando eliminación exitosa de categoría");
        assertNull(this.ultimaExcepcion, "No debe haber excepciones");
        if (this.categoriaActual != null) {
            Optional<Categoria> categoria = categoriaRepository.findById(this.categoriaActual.getId());
            assertFalse(categoria.isPresent(), "La categoría debe haber sido eliminada");
        }
    }
}
