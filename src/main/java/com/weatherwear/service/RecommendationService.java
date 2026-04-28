package com.weatherwear.service;

import com.weatherwear.client.llm.LlmClient;
import com.weatherwear.client.llm.LlmPromptBuilder;
import com.weatherwear.dto.recommendation.RecommendationRequest;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.repository.UserPreferenceRepository;
import com.weatherwear.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherService weatherService;
    private final LlmClient llmClient;
    private final LlmPromptBuilder promptBuilder;
    private final RecommendationHistoryRepository historyRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final SecurityUtils securityUtils;

    public RecommendationResponse getRecommendation(RecommendationRequest request) {

        User user = securityUtils.getCurrentUser();

        WeatherResponse weather = getWeather(request);

        UserPreference preference = preferenceRepository.findByUser(user)
                .orElse(null);

        String prompt = promptBuilder.buildPrompt(
                weather,
                preference,
                request.getOccasion()
        );

        String recommendation = llmClient.generateRecommendation(prompt);

        saveHistory(user, weather, recommendation);

        return new RecommendationResponse(
                weather.getCity(),
                buildWeatherSummary(weather),
                recommendation
        );
    }

    private WeatherResponse getWeather(RecommendationRequest request) {
        if (request.getCity() != null && !request.getCity().isBlank()) {
            return weatherService.getWeatherByCity(request.getCity());
        } else {
            return weatherService.getWeatherByCoordinates(
                    request.getLatitude(),
                    request.getLongitude()
            );
        }
    }

    private void saveHistory(User user, WeatherResponse weather, String recommendation) {
        RecommendationHistory history = RecommendationHistory.builder()
                .user(user)
                .city(weather.getCity())
                .weatherSummary(buildWeatherSummary(weather))
                .recommendationText(recommendation)
                .build();

        historyRepository.save(history);
    }

    private String buildWeatherSummary(WeatherResponse weather) {
        return "Temp: " + weather.getTemperature() +
                ", Feels: " + weather.getFeelsLike() +
                ", Condition: " + weather.getCondition();
    }
}
