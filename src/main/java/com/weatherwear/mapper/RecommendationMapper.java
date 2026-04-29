package com.weatherwear.mapper;

import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationResponse toResponse(
            WeatherResponse weather,
            String recommendation
    ) {
        return new RecommendationResponse(
                weather.getCity(),
                buildWeatherSummary(weather),
                recommendation
        );
    }

    public RecommendationResponse toResponse(RecommendationHistory history) {
        return new RecommendationResponse(
                history.getCity(),
                history.getWeatherSummary(),
                history.getRecommendationText()
        );
    }

    public HistoryResponse toHistoryResponse(RecommendationHistory history) {
        return new HistoryResponse(
                history.getId(),
                history.getCity(),
                history.getWeatherSummary(),
                history.getRecommendationText(),
                history.getCreatedAt()
        );
    }

    public RecommendationHistory toHistory(
            User user,
            WeatherResponse weather,
            String recommendation
    ) {
        return RecommendationHistory.builder()
                .user(user)
                .city(weather.getCity())
                .weatherSummary(buildWeatherSummary(weather))
                .recommendationText(recommendation)
                .build();
    }

    public String buildWeatherSummary(WeatherResponse weather) {
        if (weather == null) {
            return "Weather data unavailable";
        }

        return "Temp: " + valueOrUnknown(weather.getTemperature()) +
                ", Feels: " + valueOrUnknown(weather.getFeelsLike()) +
                ", Condition: " + valueOrUnknown(weather.getCondition());
    }

    private String valueOrUnknown(Object value) {
        return value != null ? value.toString() : "unknown";
    }
}
