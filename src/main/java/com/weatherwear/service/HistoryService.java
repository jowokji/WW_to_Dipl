package com.weatherwear.service;

import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
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

    public List<HistoryResponse> getCurrentUserHistory() {
        User user = securityUtils.getCurrentUser();

        return historyRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void clearCurrentUserHistory() {
        User user = securityUtils.getCurrentUser();
        historyRepository.deleteByUser(user);
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