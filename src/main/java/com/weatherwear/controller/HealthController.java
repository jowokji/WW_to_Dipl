package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Public service status endpoint.")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Get service health", description = "Checks whether the backend process is running.")
    @ApiResponse(
            responseCode = "200",
            description = "Service is running.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(value = OpenApiExamples.HEALTH_RESPONSE)
            )
    )
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "WeatherWear Backend",
                "timestamp", LocalDateTime.now()
        );
    }
}
