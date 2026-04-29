package com.weatherwear.controller;

import com.weatherwear.common.WeatherCondition;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = WeatherController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void getWeatherByCity_returnsOk() throws Exception {
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weatherResponse());

        mockMvc.perform(get("/weather").param("city", "Vilnius"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Vilnius"))
                .andExpect(jsonPath("$.condition").value("CLOUDS"));
    }

    @Test
    void getWeather_missingCity_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/weather"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWeather_unauthorized_returnsUnauthorized() throws Exception {
        when(weatherService.getWeatherByCity("Vilnius"))
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(get("/weather").param("city", "Vilnius"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWeather_notFound_returnsNotFound() throws Exception {
        when(weatherService.getWeatherByCity("Unknown"))
                .thenThrow(new ResourceNotFoundException("Weather not found"));

        mockMvc.perform(get("/weather").param("city", "Unknown"))
                .andExpect(status().isNotFound());
    }

    private WeatherResponse weatherResponse() {
        return new WeatherResponse(
                "Vilnius",
                54.6872,
                25.2797,
                12.0,
                10.0,
                70,
                4.5,
                WeatherCondition.CLOUDS,
                0.0,
                false
        );
    }
}
