package com.weatherwear.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Standard API error response produced by GlobalExceptionHandler.")
public class ErrorResponse {

    @Schema(description = "Server-side timestamp when the error response was produced.",
            example = "2026-04-30T12:45:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code.", example = "400")
    private int status;

    @Schema(description = "HTTP reason phrase.", example = "Bad Request")
    private String error;

    @Schema(description = "Human-readable error details.", example = "email: Email must be valid")
    private String message;

    @Schema(description = "Request path that produced the error.", example = "/api/auth/register")
    private String path;
}
