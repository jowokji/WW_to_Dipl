package com.weatherwear.dto.feedback;

import com.weatherwear.common.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Persisted feedback response.")
public class FeedbackResponse {

    @Schema(description = "Feedback identifier.", example = "501")
    private Long id;

    @Schema(description = "Recommendation history item receiving feedback.", example = "101")
    private Long recommendationHistoryId;

    @Schema(description = "Semantic feedback category.", example = "RATING")
    private FeedbackType feedbackType;

    @Schema(description = "Optional rating from 1 to 5.", example = "5", minimum = "1", maximum = "5")
    private Short rating;

    @Schema(description = "Optional free-text feedback comment.", example = "Useful recommendation for windy weather.")
    private String comment;

    @Schema(description = "Timestamp when feedback was created.", example = "2026-04-30T12:45:00")
    private LocalDateTime createdAt;
}
