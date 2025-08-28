package org.example.proyectofinal.cucumber.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hooks simplificados para prueba de integración con BD.
 */
public class CucumberHooks {

    private static final Logger log = LoggerFactory.getLogger(CucumberHooks.class);

    @Before
    public void setUp(Scenario scenario) {
        log.info("=== INICIANDO: {} ===", scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            log.error("❌ FALLO: {}", scenario.getName());
        } else {
            log.info("✅ ÉXITO: {}", scenario.getName());
        }
    }
}
