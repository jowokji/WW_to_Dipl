package com.weatherwear.service;

import com.weatherwear.client.weather.OpenWeatherResponse;
import com.weatherwear.client.weather.WeatherApiClient;
import com.weatherwear.common.Constants;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.WeatherCache;
import com.weatherwear.exception.WeatherApiException;
import com.weatherwear.mapper.WeatherMapper;
import com.weatherwear.repository.WeatherCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherApiClient weatherApiClient;

    @Mock
    private WeatherCacheRepository weatherCacheRepository;

    @Mock
    private WeatherMapper weatherMapper;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getWeatherByCity_fromCache() {
        WeatherCache cache = cache();
        WeatherResponse cachedResponse = weatherResponse(true);

        when(weatherCacheRepository.findTopByCityIgnoreCaseOrderByCachedAtDesc("Vilnius"))
                .thenReturn(Optional.of(cache));
        when(weatherMapper.toResponse(cache, true)).thenReturn(cachedResponse);

        WeatherResponse response = weatherService.getWeatherByCity("Vilnius");

        assertThat(response).isSameAs(cachedResponse);
        assertThat(response.isCached()).isTrue();
        verifyNoInteractions(weatherApiClient);
        verify(weatherCacheRepository, never()).save(any(WeatherCache.class));
    }

    @Test
    void getWeatherByCity_fromApi() {
        OpenWeatherResponse apiResponse = new OpenWeatherResponse();
        WeatherCache mappedCache = cache();
        WeatherCache savedCache = cache();
        WeatherResponse apiWeatherResponse = weatherResponse(false);

        when(weatherCacheRepository.findTopByCityIgnoreCaseOrderByCachedAtDesc("Vilnius"))
                .thenReturn(Optional.empty());
        when(weatherApiClient.getWeatherByCity("Vilnius")).thenReturn(apiResponse);
        when(weatherMapper.toCache(apiResponse, Constants.WEATHER_CACHE_MINUTES))
                .thenReturn(mappedCache);
        when(weatherCacheRepository.save(mappedCache)).thenReturn(savedCache);
        when(weatherMapper.toResponse(savedCache, false)).thenReturn(apiWeatherResponse);

        WeatherResponse response = weatherService.getWeatherByCity("Vilnius");

        assertThat(response).isSameAs(apiWeatherResponse);
        assertThat(response.isCached()).isFalse();
    }

    @Test
    void getWeatherByCity_apiError() {
        when(weatherCacheRepository.findTopByCityIgnoreCaseOrderByCachedAtDesc("Vilnius"))
                .thenReturn(Optional.empty());
        when(weatherApiClient.getWeatherByCity("Vilnius"))
                .thenThrow(new WeatherApiException("Weather API failed"));

        assertThatThrownBy(() -> weatherService.getWeatherByCity("Vilnius"))
                .isInstanceOf(WeatherApiException.class);

        verify(weatherCacheRepository, never()).save(any(WeatherCache.class));
        verifyNoInteractions(weatherMapper);
    }

    @Test
    void getWeatherByCoordinates_fromCache() {
        WeatherCache cache = cache();
        WeatherResponse cachedResponse = weatherResponse(true);

        when(weatherCacheRepository.findTopByLatitudeAndLongitudeOrderByCachedAtDesc(54.6872, 25.2797))
                .thenReturn(Optional.of(cache));
        when(weatherMapper.toResponse(cache, true)).thenReturn(cachedResponse);

        WeatherResponse response = weatherService.getWeatherByCoordinates(54.6872, 25.2797);

        assertThat(response).isSameAs(cachedResponse);
        assertThat(response.isCached()).isTrue();
        verifyNoInteractions(weatherApiClient);
        verify(weatherCacheRepository, never()).save(any(WeatherCache.class));
    }

    @Test
    void getWeatherByCoordinates_fromApi() {
        OpenWeatherResponse apiResponse = new OpenWeatherResponse();
        WeatherCache mappedCache = cache();
        WeatherCache savedCache = cache();
        WeatherResponse apiWeatherResponse = weatherResponse(false);

        when(weatherCacheRepository.findTopByLatitudeAndLongitudeOrderByCachedAtDesc(54.6872, 25.2797))
                .thenReturn(Optional.empty());
        when(weatherApiClient.getWeatherByCoordinates(54.6872, 25.2797)).thenReturn(apiResponse);
        when(weatherMapper.toCache(apiResponse, Constants.WEATHER_CACHE_MINUTES))
                .thenReturn(mappedCache);
        when(weatherCacheRepository.save(mappedCache)).thenReturn(savedCache);
        when(weatherMapper.toResponse(savedCache, false)).thenReturn(apiWeatherResponse);

        WeatherResponse response = weatherService.getWeatherByCoordinates(54.6872, 25.2797);

        assertThat(response).isSameAs(apiWeatherResponse);
        assertThat(response.isCached()).isFalse();
    }

    private WeatherCache cache() {
        return WeatherCache.builder()
                .id(10L)
                .city("Vilnius")
                .latitude(54.6872)
                .longitude(25.2797)
                .temperature(12.0)
                .feelsLike(10.0)
                .humidity(70)
                .windSpeed(4.5)
                .condition(WeatherCondition.CLOUDS)
                .precipitation(0.0)
                .cachedAt(LocalDateTime.now().minusMinutes(5))
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .build();
    }

    private WeatherResponse weatherResponse(boolean cached) {
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
                cached
        );
    }
}
