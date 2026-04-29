package com.weatherwear.dto.weather;

import com.weatherwear.common.WeatherCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherResponse {

    private String city;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Double windSpeed;
    private WeatherCondition condition;
    private Double precipitation;
    private boolean cached;
}