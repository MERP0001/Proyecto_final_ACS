package org.example.proyectofinal.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // Esta clase vacía es necesaria para la configuración de Cucumber con Spring Boot
} 