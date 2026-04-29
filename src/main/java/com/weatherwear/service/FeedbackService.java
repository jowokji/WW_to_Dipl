package com.weatherwear.service;

import com.weatherwear.common.FeedbackType;
import com.weatherwear.dto.feedback.FeedbackRequest;
import com.weatherwear.dto.feedback.FeedbackResponse;
import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserFeedback;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.repository.RecommendationHistoryRepository;
import com.weatherwear.repository.UserFeedbackRepository;
import com.weatherwear.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final UserFeedbackRepository feedbackRepository;
    private final RecommendationHistoryRepository historyRepository;
    private final SecurityUtils securityUtils;

    public FeedbackResponse createFeedback(FeedbackRequest request) {
        User user = securityUtils.getCurrentUser();
        RecommendationHistory history = getCurrentUserHistory(
                request.getRecommendationHistoryId(),
                user
        );

        UserFeedback feedback = UserFeedback.builder()
                .recommendationHistory(history)
                .user(user)
                .feedbackType(typeOrDefault(request.getFeedbackType()))
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return toResponse(feedbackRepository.save(feedback));
    }

    public List<FeedbackResponse> getCurrentUserFeedback() {
        User user = securityUtils.getCurrentUser();

        return feedbackRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FeedbackResponse> getCurrentUserFeedbackForRecommendation(
            Long recommendationHistoryId
    ) {
        User user = securityUtils.getCurrentUser();
        RecommendationHistory history = getCurrentUserHistory(
                recommendationHistoryId,
                user
        );

        return feedbackRepository
                .findByRecommendationHistoryAndUserOrderByCreatedAtDesc(history, user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteCurrentUserFeedback(Long feedbackId) {
        User user = securityUtils.getCurrentUser();

        UserFeedback feedback = feedbackRepository.findByIdAndUser(feedbackId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        feedbackRepository.delete(feedback);
    }

    private RecommendationHistory getCurrentUserHistory(Long historyId, User user) {
        return historyRepository.findByIdAndUser(historyId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recommendation history not found"
                ));
    }

    private FeedbackType typeOrDefault(FeedbackType feedbackType) {
        return feedbackType != null ? feedbackType : FeedbackType.RATING;
    }

    private FeedbackResponse toResponse(UserFeedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getRecommendationHistory().getId(),
                feedback.getFeedbackType(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt()
        );
    }
}
