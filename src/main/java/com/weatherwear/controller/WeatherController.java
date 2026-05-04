package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import com.weatherwear.dto.error.ErrorResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Weather", description = "Authenticated current weather lookup endpoints.")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    @Operation(
            summary = "Get weather by city",
            description = "Returns current weather for a city. Cached weather records are reused for 30 minutes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current weather for the requested city.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WeatherResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.WEATHER_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "City query parameter is missing or invalid.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is missing, invalid, or expired.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.UNAUTHORIZED_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "OpenWeather dependency failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.BAD_GATEWAY_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.SERVER_ERROR)
                    )
            )
    })
    public WeatherResponse getWeatherByCity(
            @RequestParam
            @Parameter(
                    description = "City name. Maximum length is 120 characters.",
                    required = true,
                    example = "Vilnius"
            )
            @NotBlank(message = "City is required")
            @Size(max = 120, message = "City must be at most 120 characters")
            String city
    ) {
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/coordinates")
    @Operation(
            summary = "Get weather by coordinates",
            description = "Returns current weather for valid latitude and longitude coordinates."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current weather for the requested coordinates.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WeatherResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.CACHED_WEATHER_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Coordinates are missing, malformed, or outside the allowed range.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is missing, invalid, or expired.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.UNAUTHORIZED_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "OpenWeather dependency failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.BAD_GATEWAY_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.SERVER_ERROR)
                    )
            )
    })
    public WeatherResponse getWeatherByCoordinates(
            @RequestParam
            @Parameter(
                    description = "Latitude in decimal degrees. Valid range is -90 to 90.",
                    required = true,
                    example = "54.6872"
            )
            @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
            @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
            Double lat,
            @RequestParam
            @Parameter(
                    description = "Longitude in decimal degrees. Valid range is -180 to 180.",
                    required = true,
                    example = "25.2797"
            )
            @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
            @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
            Double lon
    ) {
        return weatherService.getWeatherByCoordinates(lat, lon);
    }
}
