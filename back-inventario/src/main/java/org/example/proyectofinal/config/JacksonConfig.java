package org.example.proyectofinal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .modules(new Hibernate6Module(), new JavaTimeModule())
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
