package com.weatherwear.service;

import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final RecommendationHistoryRepository historyRepository;
    private final SecurityUtils securityUtils;

    public HistoryResponse saveHistory(
            User user,
            WeatherResponse weather,
            String recommendationText
    ) {
        RecommendationHistory history = RecommendationHistory.builder()
                .user(user)
                .city(weather.getCity())
                .weatherSummary(buildWeatherSummary(weather))
                .recommendationText(recommendationText)
                .build();

        return toResponse(historyRepository.save(history));
    }

    public List<HistoryResponse> getCurrentUserHistory() {
        User user = securityUtils.getCurrentUser();

        return historyRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HistoryResponse getHistoryDetails(Long historyId) {
        User user = securityUtils.getCurrentUser();

        return historyRepository.findByIdAndUser(historyId, user)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recommendation history not found"
                ));
    }

    @Transactional
    public void clearCurrentUserHistory() {
        User user = securityUtils.getCurrentUser();
        historyRepository.deleteByUser(user);
    }

    private String buildWeatherSummary(WeatherResponse weather) {
        return "Temp: " + weather.getTemperature() +
                ", Feels: " + weather.getFeelsLike() +
                ", Condition: " + weather.getCondition();
    }

    private HistoryResponse toResponse(RecommendationHistory history) {
        return new HistoryResponse(
                history.getId(),
                history.getCity(),
                history.getWeatherSummary(),
                history.getRecommendationText(),
                history.getCreatedAt()
        );
    }
}
