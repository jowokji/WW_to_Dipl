package com.weatherwear.dto.feedback;

import com.weatherwear.common.FeedbackType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

    @NotNull(message = "Recommendation history id is required")
    private Long recommendationHistoryId;

    private FeedbackType feedbackType;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Short rating;

    private String comment;

    @AssertTrue(message = "Rating or comment is required")
    public boolean hasFeedbackContent() {
        return rating != null || (comment != null && !comment.isBlank());
    }
}
