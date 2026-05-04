package com.weatherwear.dto.feedback;

import com.weatherwear.common.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Feedback request for one recommendation history item.")
public class FeedbackRequest {

    @NotNull(message = "Recommendation history id is required")
    @Schema(description = "Recommendation history item receiving feedback.", example = "101")
    private Long recommendationHistoryId;

    @Schema(description = "Semantic feedback category.", example = "RATING")
    private FeedbackType feedbackType;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Optional rating from 1 to 5.", example = "5", minimum = "1", maximum = "5")
    private Short rating;

    @Schema(description = "Optional free-text feedback comment.", example = "Useful recommendation for windy weather.")
    private String comment;

    @AssertTrue(message = "Rating or comment is required")
    @Schema(hidden = true)
    public boolean hasFeedbackContent() {
        return rating != null || (comment != null && !comment.isBlank());
    }
}
