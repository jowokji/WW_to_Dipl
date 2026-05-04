package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Preferred clothing style category.")
public enum StylePreference {
    CASUAL,
    BUSINESS,
    SPORTY,
    STREETWEAR,
    ELEGANT,
    MINIMALIST
}
