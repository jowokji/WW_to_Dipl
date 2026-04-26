package com.weatherwear.mapper;

import com.weatherwear.client.weather.OpenWeatherResponse;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.WeatherCache;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WeatherMapper {

    public WeatherCache toCache(OpenWeatherResponse response, int cacheMinutes) {
        WeatherCondition condition = mapCondition(response);

        return WeatherCache.builder()
                .city(response.getName())
                .latitude(response.getCoord() != null ? response.getCoord().getLat() : null)
                .longitude(response.getCoord() != null ? response.getCoord().getLon() : null)
                .temperature(response.getMain() != null ? response.getMain().getTemp() : null)
                .feelsLike(response.getMain() != null ? response.getMain().getFeelsLike() : null)
                .humidity(response.getMain() != null ? response.getMain().getHumidity() : null)
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : null)
                .condition(condition)
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

    private WeatherCondition mapCondition(OpenWeatherResponse response) {
        if (response.getWeather() == null || response.getWeather().isEmpty()) {
            return WeatherCondition.UNKNOWN;
        }

        String condition = response.getWeather().get(0).getMain();

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
            case "MIST" -> WeatherCondition.MIST;
            case "FOG", "HAZE" -> WeatherCondition.FOG;
            default -> WeatherCondition.UNKNOWN;
        };
    }

    private Double getPrecipitation(OpenWeatherResponse response) {
        if (response.getRain() != null && response.getRain().getOneHour() != null) {
            return response.getRain().getOneHour();
        }

        if (response.getSnow() != null && response.getSnow().getOneHour() != null) {
            return response.getSnow().getOneHour();
        }

        return 0.0;
    }
}