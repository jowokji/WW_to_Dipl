package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Expected activity intensity used to adjust clothing warmth and breathability.")
public enum ActivityLevel {
    LOW("Low activity, mostly standing or sitting"),
    MEDIUM("Moderate activity, regular walking"),
    HIGH("High activity, sport or intensive movement");

    private final String description;

    ActivityLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
