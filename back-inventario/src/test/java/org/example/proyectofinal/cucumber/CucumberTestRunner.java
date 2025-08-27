package org.example.proyectofinal.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Clase principal para ejecutar todas las pruebas de Cucumber.
 * Configura el runner de Cucumber con JUnit 5 Platform.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.example.proyectofinal.cucumber")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, 
        value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json, junit:target/cucumber-reports/cucumber.xml")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "not @ignore")
public class CucumberTestRunner {
    // Esta clase actúa como punto de entrada para las pruebas de Cucumber
    // No necesita implementación, las anotaciones configuran todo
}
