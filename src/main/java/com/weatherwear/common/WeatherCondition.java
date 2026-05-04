package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Normalized weather condition returned by the upstream provider.")
public enum WeatherCondition {
    CLEAR,
    CLOUDS,
    RAIN,
    DRIZZLE,
    THUNDERSTORM,
    SNOW,
    MIST,
    FOG,
    WIND,
    UNKNOWN
}
