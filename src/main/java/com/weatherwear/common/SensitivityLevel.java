package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User sensitivity level for weather factors.")
public enum SensitivityLevel {
    LOW,
    MEDIUM,
    HIGH
}
