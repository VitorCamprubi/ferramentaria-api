package com.vitorcamprubi.ferramentaria.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Ferramentaria API")
                .version("0.0.1")
                .description("Controle de manutenção de moldes de injeção aplicando Strategy, "
                        + "Chain of Responsibility, Facade e Singleton com Spring Boot"));
    }
}
