package com.weatherwear.controller;

import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public WeatherResponse getWeatherByCity(
            @RequestParam String city
    ) {
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/coordinates")
    public WeatherResponse getWeatherByCoordinates(
            @RequestParam Double lat,
            @RequestParam Double lon
    ) {
        return weatherService.getWeatherByCoordinates(lat, lon);
    }
}