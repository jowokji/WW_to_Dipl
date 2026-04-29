package com.weatherwear.service;

import com.weatherwear.client.weather.OpenWeatherResponse;
import com.weatherwear.client.weather.WeatherApiClient;
import com.weatherwear.common.Constants;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.WeatherCache;
import com.weatherwear.mapper.WeatherMapper;
import com.weatherwear.repository.WeatherCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiClient weatherApiClient;
    private final WeatherCacheRepository weatherCacheRepository;
    private final WeatherMapper weatherMapper;

    public WeatherResponse getWeatherByCity(String city) {
        return weatherCacheRepository.findTopByCityIgnoreCaseOrderByCachedAtDesc(city)
                .filter(cache -> !cache.isExpired())
                .map(cache -> weatherMapper.toResponse(cache, true))
                .orElseGet(() -> fetchAndCacheWeatherByCity(city));
    }

    public WeatherResponse getWeatherByCoordinates(Double lat, Double lon) {
        return weatherCacheRepository.findTopByLatitudeAndLongitudeOrderByCachedAtDesc(lat, lon)
                .filter(cache -> !cache.isExpired())
                .map(cache -> weatherMapper.toResponse(cache, true))
                .orElseGet(() -> fetchAndCacheWeatherByCoordinates(lat, lon));
    }

    private WeatherResponse fetchAndCacheWeatherByCoordinates(Double lat, Double lon) {
        OpenWeatherResponse apiResponse = weatherApiClient.getWeatherByCoordinates(lat, lon);

        WeatherCache cache = weatherMapper.toCache(
                apiResponse,
                Constants.WEATHER_CACHE_MINUTES
        );

        WeatherCache savedCache = weatherCacheRepository.save(cache);

        return weatherMapper.toResponse(savedCache, false);
    }

    private WeatherResponse fetchAndCacheWeatherByCity(String city) {
        OpenWeatherResponse apiResponse = weatherApiClient.getWeatherByCity(city);

        WeatherCache cache = weatherMapper.toCache(
                apiResponse,
                Constants.WEATHER_CACHE_MINUTES
        );

        WeatherCache savedCache = weatherCacheRepository.save(cache);

        return weatherMapper.toResponse(savedCache, false);
    }
}
