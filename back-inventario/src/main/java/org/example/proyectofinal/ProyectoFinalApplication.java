package org.example.proyectofinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "org.example.proyectofinal")
public class ProyectoFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoFinalApplication.class, args);
    }

}
