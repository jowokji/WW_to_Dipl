package com.weatherwear.client.llm;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.UserPreference;
import org.springframework.stereotype.Component;

@Component
public class LlmPromptBuilder {

    public String buildPrompt(
            WeatherResponse weather,
            UserPreference preference,
            String occasion
    ) {
        return """
                You are a professional stylist.
                Always answer in English, even if the occasion or user context is written in another language.

                Weather:
                Temperature: %s C
                Feels like: %s C
                Wind: %s m/s
                Condition: %s
                Humidity: %s%%

                User preferences:
                Style: %s
                Cold sensitivity: %s
                Heat sensitivity: %s
                Wind sensitivity: %s
                Rain sensitivity: %s
                Maximum layers: %s
                Prefers headwear: %s
                Prefers waterproof items: %s
                Activity level: %s
                Preferred colors: %s
                Avoid items: %s

                Occasion: %s

                Give a short practical recommendation in no more than 90 words:
                1. Top: one short line
                2. Bottom: one short line
                3. Shoes: one short line
                4. Accessories: one short line
                5. Why: one short line
                """.formatted(
                weather.getTemperature(),
                weather.getFeelsLike(),
                weather.getWindSpeed(),
                weather.getCondition(),
                weather.getHumidity(),
                stylePreference(preference),
                coldSensitivity(preference),
                heatSensitivity(preference),
                windSensitivity(preference),
                rainSensitivity(preference),
                maxLayers(preference),
                prefersHeadwear(preference),
                prefersWaterproof(preference),
                activityLevel(preference),
                textOrDefault(preference != null ? preference.getPreferredColors() : null),
                textOrDefault(preference != null ? preference.getAvoidItems() : null),
                occasion != null ? occasion : "casual"
        );
    }

    private StylePreference stylePreference(UserPreference preference) {
        return preference != null && preference.getStylePreference() != null
                ? preference.getStylePreference()
                : StylePreference.CASUAL;
    }

    private SensitivityLevel coldSensitivity(UserPreference preference) {
        return preference != null && preference.getColdSensitivity() != null
                ? preference.getColdSensitivity()
                : SensitivityLevel.MEDIUM;
    }

    private SensitivityLevel heatSensitivity(UserPreference preference) {
        return preference != null && preference.getHeatSensitivity() != null
                ? preference.getHeatSensitivity()
                : SensitivityLevel.MEDIUM;
    }

    private SensitivityLevel windSensitivity(UserPreference preference) {
        return preference != null && preference.getWindSensitivity() != null
                ? preference.getWindSensitivity()
                : SensitivityLevel.MEDIUM;
    }

    private SensitivityLevel rainSensitivity(UserPreference preference) {
        return preference != null && preference.getRainSensitivity() != null
                ? preference.getRainSensitivity()
                : SensitivityLevel.MEDIUM;
    }

    private Short maxLayers(UserPreference preference) {
        return preference != null && preference.getMaxLayers() != null
                ? preference.getMaxLayers()
                : 3;
    }

    private Boolean prefersHeadwear(UserPreference preference) {
        return preference != null && preference.getPrefersHeadwear() != null
                ? preference.getPrefersHeadwear()
                : false;
    }

    private Boolean prefersWaterproof(UserPreference preference) {
        return preference != null && preference.getPrefersWaterproof() != null
                ? preference.getPrefersWaterproof()
                : false;
    }

    private ActivityLevel activityLevel(UserPreference preference) {
        return preference != null && preference.getActivityLevel() != null
                ? preference.getActivityLevel()
                : ActivityLevel.MEDIUM;
    }

    private String textOrDefault(String value) {
        return value != null && !value.isBlank() ? value : "not specified";
    }
}
