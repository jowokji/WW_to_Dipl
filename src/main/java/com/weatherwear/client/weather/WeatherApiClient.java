package com.weatherwear.client.weather;

import com.weatherwear.exception.WeatherApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WeatherApiClient {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public WeatherApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpenWeatherResponse getWeatherByCity(String city) {
        if (!StringUtils.hasText(city)) {
            throw new WeatherApiException("City is required");
        }

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", city.trim())
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .toUriString();

        return fetchWeather(url);
    }

    public OpenWeatherResponse getWeatherByCoordinates(Double lat, Double lon) {
        if (lat == null || lon == null) {
            throw new WeatherApiException("Latitude and longitude are required");
        }

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .toUriString();

        return fetchWeather(url);
    }

    private OpenWeatherResponse fetchWeather(String url) {
        validateConfiguration();

        try {
            OpenWeatherResponse response = restTemplate.getForObject(url, OpenWeatherResponse.class);
            if (response == null) {
                throw new WeatherApiException("Weather API returned empty response");
            }
            return response;
        } catch (HttpStatusCodeException ex) {
            throw new WeatherApiException(
                    "Weather API returned status " + ex.getStatusCode().value(),
                    ex
            );
        } catch (RestClientException ex) {
            throw new WeatherApiException("Failed to fetch weather data", ex);
        }
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(apiUrl)) {
            throw new WeatherApiException("Weather API URL is not configured");
        }

        if (!StringUtils.hasText(apiKey) || "your-weather-api-key".equals(apiKey)) {
            throw new WeatherApiException("Weather API key is not configured");
        }
    }
}
