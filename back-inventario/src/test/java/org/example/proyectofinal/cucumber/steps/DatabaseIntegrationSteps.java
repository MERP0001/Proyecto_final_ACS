package org.example.proyectofinal.cucumber.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps ultra-simplificados para pruebas de integración con BD.
 */
public class DatabaseIntegrationSteps {

    private static final Logger log = LoggerFactory.getLogger(DatabaseIntegrationSteps.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long categoriaIdCreada;
    private boolean operacionExitosa;

    @Dado("que la base de datos está disponible")
    public void que_la_base_de_datos_esta_disponible() {
        log.info("Verificando que la base de datos H2 está disponible");
        assertNotNull(jdbcTemplate, "JdbcTemplate debe estar disponible");
        // Verificar conectividad con una consulta simple
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM categorias", Integer.class);
        operacionExitosa = true;
        log.info("Base de datos H2 disponible. Categorías existentes: {}", count);
    }

    @Cuando("creo una categoria de prueba")
    public void creo_una_categoria_de_prueba() {
        log.info("Creando categoría de prueba en la base de datos");
        try {
            // Usar INSERT simple sin RETURNING - H2 compatible
            String sql = "INSERT INTO categorias (nombre, descripcion, activo, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            int rowsAffected = jdbcTemplate.update(sql, "Prueba Cucumber", "Categoría creada por Cucumber", true);
            
            if (rowsAffected > 0) {
                // Obtener el ID de la categoría recién creada
                categoriaIdCreada = jdbcTemplate.queryForObject(
                    "SELECT id FROM categorias WHERE nombre = ? ORDER BY id DESC LIMIT 1", 
                    Long.class, 
                    "Prueba Cucumber"
                );
                operacionExitosa = true;
                log.info("Categoría creada con ID: {}", categoriaIdCreada);
            } else {
                operacionExitosa = false;
                log.error("No se pudo insertar la categoría");
            }
        } catch (Exception e) {
            log.error("Error al crear categoría: {}", e.getMessage());
            operacionExitosa = false;
        }
    }

    @Entonces("la categoria debe guardarse en la base de datos")
    public void la_categoria_debe_guardarse_en_la_base_de_datos() {
        log.info("Verificando que la categoría se guardó correctamente");
        assertTrue(operacionExitosa, "La operación debe ser exitosa");
        assertNotNull(categoriaIdCreada, "La categoría debe tener un ID");
        
        // Verificar que la categoría existe en la base de datos
        String nombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM categorias WHERE id = ?", 
            String.class, 
            categoriaIdCreada
        );
        assertEquals("Prueba Cucumber", nombre, "El nombre debe coincidir");
        log.info("✅ Integración con base de datos exitosa - Categoría ID: {}", categoriaIdCreada);
    }
}