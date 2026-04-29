package com.weatherwear.service;

import com.weatherwear.client.llm.LlmClient;
import com.weatherwear.client.llm.LlmPromptBuilder;
import com.weatherwear.common.Role;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.recommendation.RecommendationRequest;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import com.weatherwear.exception.LlmApiException;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.repository.UserPreferenceRepository;
import com.weatherwear.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private LlmClient llmClient;

    @Mock
    private LlmPromptBuilder promptBuilder;

    @Mock
    private RecommendationHistoryRepository historyRepository;

    @Mock
    private UserPreferenceRepository preferenceRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void generateRecommendation_success() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        RecommendationRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weather);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(promptBuilder.buildPrompt(weather, null, "work")).thenReturn("prompt");
        when(llmClient.generateRecommendation("prompt")).thenReturn("Wear a jacket");

        RecommendationResponse response = recommendationService.getRecommendation(request);

        assertThat(response.getCity()).isEqualTo("Vilnius");
        assertThat(response.getWeatherSummary())
                .isEqualTo("Temp: 12.0, Feels: 10.0, Condition: CLOUDS");
        assertThat(response.getRecommendation()).isEqualTo("Wear a jacket");
    }

    @Test
    void generateRecommendation_withUserPreferences() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        RecommendationRequest request = request();
        UserPreference preference = preference(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weather);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));
        when(promptBuilder.buildPrompt(weather, preference, "work")).thenReturn("prompt with prefs");
        when(llmClient.generateRecommendation("prompt with prefs")).thenReturn("Wear a coat");

        RecommendationResponse response = recommendationService.getRecommendation(request);

        assertThat(response.getRecommendation()).isEqualTo("Wear a coat");
        verify(promptBuilder).buildPrompt(weather, preference, "work");
    }

    @Test
    void generateRecommendation_savesToHistory() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        RecommendationRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weather);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(promptBuilder.buildPrompt(weather, null, "work")).thenReturn("prompt");
        when(llmClient.generateRecommendation("prompt")).thenReturn("Wear a jacket");

        recommendationService.getRecommendation(request);

        ArgumentCaptor<RecommendationHistory> historyCaptor =
                ArgumentCaptor.forClass(RecommendationHistory.class);
        verify(historyRepository).save(historyCaptor.capture());

        RecommendationHistory history = historyCaptor.getValue();
        assertThat(history.getUser()).isSameAs(user);
        assertThat(history.getCity()).isEqualTo("Vilnius");
        assertThat(history.getWeatherSummary())
                .isEqualTo("Temp: 12.0, Feels: 10.0, Condition: CLOUDS");
        assertThat(history.getRecommendationText()).isEqualTo("Wear a jacket");
    }

    @Test
    void generateRecommendation_llmError() {
        User user = user();
        WeatherResponse weather = weatherResponse();
        RecommendationRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weather);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(promptBuilder.buildPrompt(weather, null, "work")).thenReturn("prompt");
        when(llmClient.generateRecommendation("prompt"))
                .thenThrow(new LlmApiException("LLM failed"));

        assertThatThrownBy(() -> recommendationService.getRecommendation(request))
                .isInstanceOf(LlmApiException.class);

        verify(historyRepository, never()).save(any(RecommendationHistory.class));
    }

    private RecommendationRequest request() {
        RecommendationRequest request = new RecommendationRequest();
        request.setCity("Vilnius");
        request.setOccasion("work");
        return request;
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

    private UserPreference preference(User user) {
        return UserPreference.builder()
                .id(20L)
                .user(user)
                .stylePreference(StylePreference.BUSINESS)
                .coldSensitivity(SensitivityLevel.HIGH)
                .heatSensitivity(SensitivityLevel.LOW)
                .build();
    }
}
