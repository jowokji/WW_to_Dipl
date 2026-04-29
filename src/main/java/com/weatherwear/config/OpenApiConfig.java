package com.weatherwear.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${openapi.server-url:}")
    private String serverUrl;

    @Bean
    public OpenAPI weatherWearOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("WeatherWear API")
                        .description("REST API for weather forecast, clothing recommendations and AI assistant")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dubrovin Mikita")
                                .email("dubrovin.nikita@student.ehu.lt")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME)
                )
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );

        if (StringUtils.hasText(serverUrl)) {
            openAPI.setServers(List.of(new Server().url(serverUrl)));
        }

        return openAPI;
    }
}
