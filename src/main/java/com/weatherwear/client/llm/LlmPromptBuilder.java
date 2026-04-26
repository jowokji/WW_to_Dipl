package com.weatherwear.client.llm;

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

                Weather:
                Temperature: %s°C
                Feels like: %s°C
                Wind: %s m/s
                Condition: %s
                Humidity: %s%%

                User preferences:
                Style: %s
                Cold sensitivity: %s
                Heat sensitivity: %s
                Preferred colors: %s
                Avoid items: %s

                Occasion: %s

                Give a clear recommendation:
                1. Top
                2. Bottom
                3. Shoes
                4. Accessories
                5. Short explanation
                """.formatted(
                weather.getTemperature(),
                weather.getFeelsLike(),
                weather.getWindSpeed(),
                weather.getCondition(),
                weather.getHumidity(),
                preference.getStylePreference(),
                preference.getColdSensitivity(),
                preference.getHeatSensitivity(),
                preference.getPreferredColors(),
                preference.getAvoidItems(),
                occasion != null ? occasion : "casual"
        );
    }
}