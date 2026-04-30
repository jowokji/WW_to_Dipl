package com.weatherwear.client.weather;

import com.weatherwear.exception.WeatherApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeatherApiClientTest {

    private RestTemplate restTemplate;
    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        weatherApiClient = new WeatherApiClient(restTemplate);
        ReflectionTestUtils.setField(weatherApiClient, "apiUrl", "https://weather.example/current");
        ReflectionTestUtils.setField(weatherApiClient, "apiKey", "api-key");
    }

    @Test
    void getWeatherByCity_success() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        when(restTemplate.getForObject(
                org.mockito.ArgumentMatchers.<String>argThat(url ->
                        url.contains("q=Vilnius") && url.contains("appid=api-key")
                ),
                eq(OpenWeatherResponse.class)
        )).thenReturn(response);

        assertThat(weatherApiClient.getWeatherByCity(" Vilnius ")).isSameAs(response);
    }

    @Test
    void getWeatherByCoordinates_success() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        when(restTemplate.getForObject(
                org.mockito.ArgumentMatchers.<String>argThat(url ->
                        url.contains("lat=54.6872") && url.contains("lon=25.2797")
                ),
                eq(OpenWeatherResponse.class)
        )).thenReturn(response);

        assertThat(weatherApiClient.getWeatherByCoordinates(54.6872, 25.2797))
                .isSameAs(response);
    }

    @Test
    void getWeatherByCity_blankCity_throwsException() {
        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity(" "))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("City is required");

        verify(restTemplate, never()).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }

    @Test
    void getWeatherByCoordinates_missingCoordinate_throwsException() {
        assertThatThrownBy(() -> weatherApiClient.getWeatherByCoordinates(null, 25.2797))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Latitude and longitude are required");
    }

    @Test
    void getWeatherByCity_missingApiKey_throwsException() {
        ReflectionTestUtils.setField(weatherApiClient, "apiKey", "");

        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity("Vilnius"))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Weather API key is not configured");
    }

    @Test
    void getWeatherByCity_missingApiUrl_throwsException() {
        ReflectionTestUtils.setField(weatherApiClient, "apiUrl", "");

        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity("Vilnius"))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Weather API URL is not configured");
    }

    @Test
    void getWeatherByCity_emptyApiResponse_throwsException() {
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity("Vilnius"))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Weather API returned empty response");
    }

    @Test
    void getWeatherByCity_httpStatusError_throwsException() {
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        StandardCharsets.UTF_8
                ));

        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity("Unknown"))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Weather API returned status 404");
    }

    @Test
    void getWeatherByCity_restClientError_throwsException() {
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenThrow(new RestClientException("Network error"));

        assertThatThrownBy(() -> weatherApiClient.getWeatherByCity("Vilnius"))
                .isInstanceOf(WeatherApiException.class)
                .hasMessage("Failed to fetch weather data");
    }
}
