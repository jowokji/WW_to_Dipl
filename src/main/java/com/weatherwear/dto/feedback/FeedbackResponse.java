package com.weatherwear.dto.feedback;

import com.weatherwear.common.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedbackResponse {

    private Long id;
    private Long recommendationHistoryId;
    private FeedbackType feedbackType;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;
}
