package com.smartcourier.admin.config;

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
        String serverUrl = "http://localhost:8084";
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description("Admin Service")))
                .info(new Info()
                        .title("SmartCourier Admin Service API")
                        .version("1.0.0")
                        .description("API documentation for Admin Service - handles dashboard KPIs, exception handling, reports, and hub management")
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