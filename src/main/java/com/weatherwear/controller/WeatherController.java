package com.weatherwear.controller;

import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.service.WeatherService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public WeatherResponse getWeatherByCity(
            @RequestParam
            @NotBlank(message = "City is required")
            @Size(max = 120, message = "City must be at most 120 characters")
            String city
    ) {
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/coordinates")
    public WeatherResponse getWeatherByCoordinates(
            @RequestParam
            @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
            @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
            Double lat,
            @RequestParam
            @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
            @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
            Double lon
    ) {
        return weatherService.getWeatherByCoordinates(lat, lon);
    }
}
