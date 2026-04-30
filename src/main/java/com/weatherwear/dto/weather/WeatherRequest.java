package com.weatherwear.dto.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherRequest {

    private String city;

    private Double latitude;

    private Double longitude;
}
