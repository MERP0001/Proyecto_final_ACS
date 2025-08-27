package org.example.proyectofinal.regression;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Suite de pruebas de regresión
 * Ejecuta todas las pruebas críticas del sistema para asegurar que 
 * los cambios no rompan funcionalidades existentes
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.example.proyectofinal.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/regression-reports/cucumber.html, json:target/regression-reports/cucumber.json")
@ActiveProfiles("test")
public class RegressionTestSuite {
    // Esta clase ejecuta automáticamente todas las pruebas de regresión
}
