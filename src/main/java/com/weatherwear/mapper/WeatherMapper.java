package com.weatherwear.mapper;

import com.weatherwear.client.weather.OpenWeatherResponse;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.WeatherCache;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class WeatherMapper {

    public WeatherCache toCache(OpenWeatherResponse response, int cacheMinutes) {
        Objects.requireNonNull(response, "OpenWeather response must not be null");

        return WeatherCache.builder()
                .city(getCity(response))
                .latitude(getLat(response))
                .longitude(getLon(response))
                .temperature(getTemp(response))
                .feelsLike(getFeelsLike(response))
                .humidity(getHumidity(response))
                .windSpeed(getWind(response))
                .condition(mapCondition(response))
                .precipitation(getPrecipitation(response))
                .cachedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(cacheMinutes))
                .build();
    }

    public WeatherResponse toResponse(WeatherCache cache, boolean cached) {
        return new WeatherResponse(
                cache.getCity(),
                cache.getLatitude(),
                cache.getLongitude(),
                cache.getTemperature(),
                cache.getFeelsLike(),
                cache.getHumidity(),
                cache.getWindSpeed(),
                cache.getCondition(),
                cache.getPrecipitation(),
                cached
        );
    }

    // --- helpers ---

    private String getCity(OpenWeatherResponse r) {
        if (r.getName() == null || r.getName().isBlank()) {
            return "Unknown";
        }

        return r.getName();
    }

    private Double getLat(OpenWeatherResponse r) {
        return r.getCoord() != null ? r.getCoord().getLat() : null;
    }

    private Double getLon(OpenWeatherResponse r) {
        return r.getCoord() != null ? r.getCoord().getLon() : null;
    }

    private Double getTemp(OpenWeatherResponse r) {
        return r.getMain() != null ? r.getMain().getTemp() : null;
    }

    private Double getFeelsLike(OpenWeatherResponse r) {
        return r.getMain() != null ? r.getMain().getFeelsLike() : null;
    }

    private Integer getHumidity(OpenWeatherResponse r) {
        return r.getMain() != null ? r.getMain().getHumidity() : null;
    }

    private Double getWind(OpenWeatherResponse r) {
        return r.getWind() != null ? r.getWind().getSpeed() : null;
    }

    private WeatherCondition mapCondition(OpenWeatherResponse r) {
        if (r.getWeather() == null || r.getWeather().isEmpty()) {
            return WeatherCondition.UNKNOWN;
        }

        String condition = r.getWeather().get(0).getMain();

        if (condition == null) {
            return WeatherCondition.UNKNOWN;
        }

        return switch (condition.toUpperCase()) {
            case "CLEAR" -> WeatherCondition.CLEAR;
            case "CLOUDS" -> WeatherCondition.CLOUDS;
            case "RAIN" -> WeatherCondition.RAIN;
            case "DRIZZLE" -> WeatherCondition.DRIZZLE;
            case "THUNDERSTORM" -> WeatherCondition.THUNDERSTORM;
            case "SNOW" -> WeatherCondition.SNOW;
            case "MIST", "SMOKE" -> WeatherCondition.MIST;
            case "FOG", "HAZE", "DUST", "SAND", "ASH" -> WeatherCondition.FOG;
            case "SQUALL", "TORNADO" -> WeatherCondition.WIND;
            default -> WeatherCondition.UNKNOWN;
        };
    }

    private Double getPrecipitation(OpenWeatherResponse r) {
        if (r.getRain() != null && r.getRain().getOneHour() != null) {
            return r.getRain().getOneHour();
        }

        if (r.getSnow() != null && r.getSnow().getOneHour() != null) {
            return r.getSnow().getOneHour();
        }

        return 0.0;
    }
}
