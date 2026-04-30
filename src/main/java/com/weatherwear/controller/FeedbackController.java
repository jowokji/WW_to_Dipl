package com.weatherwear.controller;

import com.weatherwear.dto.feedback.FeedbackRequest;
import com.weatherwear.dto.feedback.FeedbackResponse;
import com.weatherwear.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public FeedbackResponse createFeedback(
            @Valid @RequestBody FeedbackRequest request
    ) {
        return feedbackService.createFeedback(request);
    }

    @GetMapping
    public List<FeedbackResponse> getFeedback() {
        return feedbackService.getCurrentUserFeedback();
    }

    @GetMapping("/recommendations/{recommendationHistoryId}")
    public List<FeedbackResponse> getRecommendationFeedback(
            @PathVariable Long recommendationHistoryId
    ) {
        return feedbackService.getCurrentUserFeedbackForRecommendation(
                recommendationHistoryId
        );
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteCurrentUserFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
