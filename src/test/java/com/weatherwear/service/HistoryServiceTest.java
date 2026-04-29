package com.weatherwear.service;

import com.weatherwear.common.Role;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private RecommendationHistoryRepository historyRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private HistoryService historyService;

    @Test
    void saveHistory_success() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 29, 12, 0);

        when(historyRepository.save(any(RecommendationHistory.class))).thenAnswer(invocation -> {
            RecommendationHistory saved = invocation.getArgument(0);
            saved.setId(100L);
            saved.setCreatedAt(createdAt);
            return saved;
        });

        HistoryResponse response = historyService.saveHistory(user, weather, "Wear a jacket");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getCity()).isEqualTo("Vilnius");
        assertThat(response.getWeatherSummary())
                .isEqualTo("Temp: 12.0, Feels: 10.0, Condition: CLOUDS");
        assertThat(response.getRecommendationText()).isEqualTo("Wear a jacket");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);

        ArgumentCaptor<RecommendationHistory> historyCaptor =
                ArgumentCaptor.forClass(RecommendationHistory.class);
        verify(historyRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getUser()).isSameAs(user);
    }

    @Test
    void getUserHistory_success() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(history(2L), history(1L)));

        List<HistoryResponse> response = historyService.getCurrentUserHistory();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo(2L);
        assertThat(response.get(1).getId()).isEqualTo(1L);
    }

    @Test
    void getUserHistory_emptyList() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of());

        List<HistoryResponse> response = historyService.getCurrentUserHistory();

        assertThat(response).isEmpty();
    }

    @Test
    void getHistoryDetails_success() {
        User user = user();
        RecommendationHistory history = history(5L);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByIdAndUser(5L, user)).thenReturn(Optional.of(history));

        HistoryResponse response = historyService.getHistoryDetails(5L);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getCity()).isEqualTo("Vilnius");
        assertThat(response.getRecommendationText()).isEqualTo("Wear a jacket");
    }

    @Test
    void clearHistory_success() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        historyService.clearCurrentUserHistory();

        verify(historyRepository).deleteByUser(user);
    }

    private User user() {
        return User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    private WeatherResponse weatherResponse() {
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

    private RecommendationHistory history(Long id) {
        return RecommendationHistory.builder()
                .id(id)
                .user(user())
                .city("Vilnius")
                .weatherSummary("Temp: 12.0, Feels: 10.0, Condition: CLOUDS")
                .recommendationText("Wear a jacket")
                .createdAt(LocalDateTime.of(2026, 4, 29, 12, 0).minusHours(id))
                .build();
    }
}
