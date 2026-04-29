package com.weatherwear.service;

import com.weatherwear.common.FeedbackType;
import com.weatherwear.common.Role;
import com.weatherwear.dto.feedback.FeedbackRequest;
import com.weatherwear.dto.feedback.FeedbackResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserFeedback;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.repository.UserFeedbackRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private UserFeedbackRepository feedbackRepository;

    @Mock
    private RecommendationHistoryRepository historyRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    void createFeedback_success() {
        User user = user();
        RecommendationHistory history = history(user);
        FeedbackRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(history));
        when(feedbackRepository.save(any(UserFeedback.class))).thenAnswer(invocation -> {
            UserFeedback feedback = invocation.getArgument(0);
            feedback.setId(1L);
            feedback.setCreatedAt(LocalDateTime.of(2026, 4, 29, 12, 0));
            return feedback;
        });

        FeedbackResponse response = feedbackService.createFeedback(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRecommendationHistoryId()).isEqualTo(10L);
        assertThat(response.getFeedbackType()).isEqualTo(FeedbackType.RATING);
        assertThat(response.getRating()).isEqualTo((short) 5);

        ArgumentCaptor<UserFeedback> feedbackCaptor =
                ArgumentCaptor.forClass(UserFeedback.class);
        verify(feedbackRepository).save(feedbackCaptor.capture());
        assertThat(feedbackCaptor.getValue().getUser()).isSameAs(user);
        assertThat(feedbackCaptor.getValue().getRecommendationHistory()).isSameAs(history);
    }

    @Test
    void createFeedback_withoutType_usesRatingDefault() {
        User user = user();
        RecommendationHistory history = history(user);
        FeedbackRequest request = request();
        request.setFeedbackType(null);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(history));
        when(feedbackRepository.save(any(UserFeedback.class))).thenAnswer(invocation -> {
            UserFeedback feedback = invocation.getArgument(0);
            feedback.setId(1L);
            feedback.setCreatedAt(LocalDateTime.of(2026, 4, 29, 12, 0));
            return feedback;
        });

        FeedbackResponse response = feedbackService.createFeedback(request);

        assertThat(response.getFeedbackType()).isEqualTo(FeedbackType.RATING);
    }

    @Test
    void createFeedback_historyNotFound_throwsException() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByIdAndUser(10L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.createFeedback(request()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recommendation history not found");
    }

    @Test
    void getCurrentUserFeedback_success() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(feedbackRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(feedback(user)));

        List<FeedbackResponse> response = feedbackService.getCurrentUserFeedback();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getCurrentUserFeedbackForRecommendation_success() {
        User user = user();
        RecommendationHistory history = history(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(historyRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(history));
        when(feedbackRepository.findByRecommendationHistoryAndUserOrderByCreatedAtDesc(history, user))
                .thenReturn(List.of(feedback(user)));

        List<FeedbackResponse> response =
                feedbackService.getCurrentUserFeedbackForRecommendation(10L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getRecommendationHistoryId()).isEqualTo(10L);
    }

    @Test
    void deleteCurrentUserFeedback_success() {
        User user = user();
        UserFeedback feedback = feedback(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(feedbackRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(feedback));

        feedbackService.deleteCurrentUserFeedback(1L);

        verify(feedbackRepository).delete(feedback);
    }

    @Test
    void deleteCurrentUserFeedback_notFound_throwsException() {
        User user = user();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(feedbackRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.deleteCurrentUserFeedback(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Feedback not found");
    }

    private FeedbackRequest request() {
        FeedbackRequest request = new FeedbackRequest();
        request.setRecommendationHistoryId(10L);
        request.setFeedbackType(FeedbackType.RATING);
        request.setRating((short) 5);
        request.setComment("Useful");
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

    private RecommendationHistory history(User user) {
        return RecommendationHistory.builder()
                .id(10L)
                .user(user)
                .city("Vilnius")
                .weatherSummary("Temp: 12.0")
                .recommendationText("Wear a jacket")
                .createdAt(LocalDateTime.of(2026, 4, 29, 12, 0))
                .build();
    }

    private UserFeedback feedback(User user) {
        return UserFeedback.builder()
                .id(1L)
                .recommendationHistory(history(user))
                .user(user)
                .feedbackType(FeedbackType.RATING)
                .rating((short) 5)
                .comment("Useful")
                .createdAt(LocalDateTime.of(2026, 4, 29, 12, 0))
                .build();
    }
}
