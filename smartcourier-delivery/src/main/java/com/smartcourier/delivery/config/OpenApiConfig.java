package com.smartcourier.delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String serverUrl = "http://localhost:8082";
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description("Delivery Service")))
                .info(new Info()
                        .title("SmartCourier Delivery Service API")
                        .version("1.0.0")
                        .description("API for managing delivery requests, quotes, and scheduling")
                        .contact(new Contact()
                                .name("SmartCourier Team")
                                .email("support@smartcourier.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
