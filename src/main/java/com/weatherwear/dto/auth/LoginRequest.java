package com.weatherwear.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request used to exchange user credentials for a JWT token.")
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Registered user email.", example = "alex@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password.", example = "securePass123", format = "password")
    private String password;
}
