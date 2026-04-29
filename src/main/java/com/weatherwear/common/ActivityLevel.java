package com.weatherwear.common;

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
