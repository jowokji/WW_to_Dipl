package com.weatherwear.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Authentication response containing the JWT access token and user identity.")
public class AuthResponse {

    @Schema(description = "JWT access token.", example = "eyJhbGciOiJIUzI1NiJ9.example.signature")
    private String token;

    @Schema(description = "Authenticated user email.", example = "alex@example.com")
    private String email;

    @Schema(description = "Application role assigned to the user.", example = "USER")
    private String role;
}
