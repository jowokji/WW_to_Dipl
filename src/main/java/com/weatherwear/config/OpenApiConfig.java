package com.weatherwear.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
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
                        .description(
                                "REST API for JWT authentication, weather lookup, "
                                        + "AI clothing recommendations, user preferences, "
                                        + "recommendation history, feedback, and style chat."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dubrovin Mikita")
                                .email("dubrovin.nikita@student.ehu.lt")
                        )
                )
                .tags(apiTags())
                .servers(apiServers())
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "JWT returned by /auth/register or /auth/login. "
                                                        + "Send it as Authorization: Bearer <token>."
                                        )
                        )
                );

        if (StringUtils.hasText(serverUrl)) {
            openAPI.setServers(List.of(new Server()
                    .url(serverUrl)
                    .description("Configured deployment server")));
        }

        return openAPI;
    }

    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Authentication")
                        .description("Registration and login endpoints that issue JWT access tokens."),
                new Tag()
                        .name("Account")
                        .description("Authenticated account lifecycle endpoints."),
                new Tag()
                        .name("Weather")
                        .description("Current weather lookup backed by OpenWeather and local cache."),
                new Tag()
                        .name("Recommendations")
                        .description("AI outfit recommendation workflow."),
                new Tag()
                        .name("Preferences")
                        .description("User style, sensitivity, and activity preferences."),
                new Tag()
                        .name("History")
                        .description("Saved recommendation history for the current user."),
                new Tag()
                        .name("Feedback")
                        .description("User feedback for generated recommendations."),
                new Tag()
                        .name("Chat")
                        .description("Weather-aware conversational style assistant."),
                new Tag()
                        .name("Health")
                        .description("Public service status endpoint.")
        );
    }

    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8090/api")
                        .description("Local development server"),
                new Server()
                        .url("https://wwtodipl-production.up.railway.app/api")
                        .description("Production server")
        );
    }
}
