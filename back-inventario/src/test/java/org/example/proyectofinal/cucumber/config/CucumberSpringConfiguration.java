package org.example.proyectofinal.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.proyectofinal.ProyectoFinalApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Configuración de contexto de Spring para Cucumber.
 * Esta clase es requerida para la integración de Cucumber con Spring Boot.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = ProyectoFinalApplication.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // Esta clase solo necesita las anotaciones, no requiere implementación
}
