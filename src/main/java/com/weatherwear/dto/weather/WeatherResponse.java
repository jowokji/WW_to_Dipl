package com.weatherwear.dto.weather;

import com.weatherwear.common.WeatherCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Normalized current weather response returned by WeatherWear.")
public class WeatherResponse {

    @Schema(description = "City resolved by the weather provider.", example = "Vilnius")
    private String city;

    @Schema(description = "Latitude in decimal degrees.", example = "54.6872")
    private Double latitude;

    @Schema(description = "Longitude in decimal degrees.", example = "25.2797")
    private Double longitude;

    @Schema(description = "Temperature in Celsius.", example = "12.4")
    private Double temperature;

    @Schema(description = "Feels-like temperature in Celsius.", example = "10.8")
    private Double feelsLike;

    @Schema(description = "Relative humidity percentage.", example = "71")
    private Integer humidity;

    @Schema(description = "Wind speed in meters per second.", example = "4.6")
    private Double windSpeed;

    @Schema(description = "Normalized weather condition.", example = "CLOUDS")
    private WeatherCondition condition;

    @Schema(description = "Precipitation volume from the upstream provider when available.", example = "0.0")
    private Double precipitation;

    @Schema(description = "True when the response came from the local weather cache.", example = "false")
    private boolean cached;
}
