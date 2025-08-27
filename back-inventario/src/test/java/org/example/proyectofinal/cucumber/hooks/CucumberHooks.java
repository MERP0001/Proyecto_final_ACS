package org.example.proyectofinal.cucumber.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.proyectofinal.cucumber.steps.AutenticacionSteps;
import org.example.proyectofinal.cucumber.steps.CategoriaSteps;
import org.example.proyectofinal.cucumber.steps.ProductoSimpleSteps;
import org.example.proyectofinal.cucumber.steps.ProductoSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Hooks de Cucumber para configurar y limpiar el estado entre escenarios.
 * Maneja la configuración de base de datos y limpieza de datos de prueba.
 */
public class CucumberHooks {

    private static final Logger log = LoggerFactory.getLogger(CucumberHooks.class);

    @Autowired(required = false)
    private ProductoSteps productoSteps;

    @Autowired(required = false)
    private ProductoSimpleSteps productoSimpleSteps;

    @Autowired(required = false)
    private CategoriaSteps categoriaSteps;

    @Autowired(required = false)
    private AutenticacionSteps autenticacionSteps;

    /**
     * Hook que se ejecuta antes de cada escenario.
     * Prepara el entorno de prueba.
     */
    @Before
    public void setUp(Scenario scenario) {
        log.info("=== INICIANDO ESCENARIO: {} ===", scenario.getName());
        log.info("Tags del escenario: {}", scenario.getSourceTagNames());
        
        // Configurar logging para el escenario
        if (scenario.getSourceTagNames().contains("@debug")) {
            log.debug("Modo debug activado para este escenario");
        }
    }

    /**
     * Hook que se ejecuta después de cada escenario.
     * Limpia el estado y datos de prueba.
     */
    @After
    public void tearDown(Scenario scenario) {
        log.info("=== FINALIZANDO ESCENARIO: {} ===", scenario.getName());
        log.info("Estado del escenario: {}", scenario.getStatus());
        
        // Limpiar estado de los steps
        limpiarEstadoSteps();
        
        // Log adicional si el escenario falló
        if (scenario.isFailed()) {
            log.error("❌ ESCENARIO FALLIDO: {}", scenario.getName());
            log.error("Detalles del fallo disponibles en los logs anteriores");
        } else {
            log.info("✅ ESCENARIO EXITOSO: {}", scenario.getName());
        }
        
        log.info("==========================================");
    }

    /**
     * Hook específico para escenarios de productos que requieren limpieza especial.
     */
    @After("@productos")
    public void tearDownProductos(Scenario scenario) {
        log.info("Limpieza específica para escenarios de productos");
        // Aquí se puede agregar limpieza específica de productos si es necesario
    }

    /**
     * Hook específico para escenarios de regresión.
     */
    @After("@regresion")
    public void tearDownRegresion(Scenario scenario) {
        log.info("Limpieza específica para escenarios de regresión");
        // Limpieza adicional para regresión si es necesario
    }

    /**
     * Hook específico para escenarios críticos que requieren verificación adicional.
     */
    @After("@critico")
    public void tearDownCritico(Scenario scenario) {
        if (scenario.isFailed()) {
            log.error("🚨 FALLO EN ESCENARIO CRÍTICO: {}", scenario.getName());
            log.error("Este fallo requiere atención inmediata");
        }
    }

    /**
     * Limpia el estado de todas las clases de steps.
     */
    private void limpiarEstadoSteps() {
        try {
            if (productoSteps != null) {
                productoSteps.limpiarEstado();
            }
            if (productoSimpleSteps != null) {
                productoSimpleSteps.limpiarEstado();
            }
            if (categoriaSteps != null) {
                categoriaSteps.limpiarEstado();
            }
            if (autenticacionSteps != null) {
                autenticacionSteps.limpiarEstado();
            }
            
            log.debug("Estado de steps limpiado correctamente");
            
        } catch (Exception e) {
            log.warn("Error al limpiar estado de steps: {}", e.getMessage());
        }
    }
}
