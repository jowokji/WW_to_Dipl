package com.weatherwear.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration request used to create a new WeatherWear user account.")
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Unique user email.", example = "alex@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(
            description = "Plain password submitted by the client. The backend stores only a BCrypt hash.",
            example = "securePass123",
            minLength = 6,
            format = "password"
    )
    private String password;
}
