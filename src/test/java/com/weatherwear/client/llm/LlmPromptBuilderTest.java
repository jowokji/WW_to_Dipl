package com.weatherwear.client.llm;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.UserPreference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmPromptBuilderTest {

    private final LlmPromptBuilder promptBuilder = new LlmPromptBuilder();

    @Test
    void buildPrompt_withUserPreferences_includesWeatherPreferencesAndOccasion() {
        UserPreference preference = UserPreference.builder()
                .stylePreference(StylePreference.BUSINESS)
                .coldSensitivity(SensitivityLevel.HIGH)
                .heatSensitivity(SensitivityLevel.LOW)
                .windSensitivity(SensitivityLevel.HIGH)
                .rainSensitivity(SensitivityLevel.HIGH)
                .maxLayers((short) 4)
                .prefersHeadwear(true)
                .prefersWaterproof(true)
                .activityLevel(ActivityLevel.HIGH)
                .preferredColors("navy")
                .avoidItems("shorts")
                .build();

        String prompt = promptBuilder.buildPrompt(weather(), preference, "work");

        assertThat(prompt)
                .contains("Always answer in English")
                .contains("Temperature: 12.0")
                .contains("Feels like: 10.0")
                .contains("Wind: 4.5")
                .contains("Condition: CLOUDS")
                .contains("Humidity: 70")
                .contains("Style: BUSINESS")
                .contains("Cold sensitivity: HIGH")
                .contains("Heat sensitivity: LOW")
                .contains("Wind sensitivity: HIGH")
                .contains("Rain sensitivity: HIGH")
                .contains("Maximum layers: 4")
                .contains("Prefers headwear: true")
                .contains("Prefers waterproof items: true")
                .contains("Activity level: HIGH")
                .contains("Preferred colors: navy")
                .contains("Avoid items: shorts")
                .contains("Occasion: work");
    }

    @Test
    void buildPrompt_withoutPreferences_usesDefaults() {
        String prompt = promptBuilder.buildPrompt(weather(), null, null);

        assertThat(prompt)
                .contains("Style: CASUAL")
                .contains("Cold sensitivity: MEDIUM")
                .contains("Heat sensitivity: MEDIUM")
                .contains("Wind sensitivity: MEDIUM")
                .contains("Rain sensitivity: MEDIUM")
                .contains("Maximum layers: 3")
                .contains("Prefers headwear: false")
                .contains("Prefers waterproof items: false")
                .contains("Activity level: MEDIUM")
                .contains("Preferred colors: not specified")
                .contains("Avoid items: not specified")
                .contains("Occasion: casual");
    }

    @Test
    void buildPrompt_withPartialPreferences_usesDefaultsForMissingFields() {
        UserPreference preference = UserPreference.builder()
                .stylePreference(StylePreference.STREETWEAR)
                .preferredColors(" ")
                .avoidItems(null)
                .build();

        String prompt = promptBuilder.buildPrompt(weather(), preference, "walk");

        assertThat(prompt)
                .contains("Style: STREETWEAR")
                .contains("Cold sensitivity: MEDIUM")
                .contains("Heat sensitivity: MEDIUM")
                .contains("Wind sensitivity: MEDIUM")
                .contains("Rain sensitivity: MEDIUM")
                .contains("Maximum layers: 3")
                .contains("Activity level: MEDIUM")
                .contains("Preferred colors: not specified")
                .contains("Avoid items: not specified")
                .contains("Occasion: walk");
    }

    private WeatherResponse weather() {
        return new WeatherResponse(
                "Vilnius",
                54.6872,
                25.2797,
                12.0,
                10.0,
                70,
                4.5,
                WeatherCondition.CLOUDS,
                0.0,
                false
        );
    }
}
