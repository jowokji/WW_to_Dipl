package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Feedback category selected by the user.")
public enum FeedbackType {
    RATING,
    LIKE,
    DISLIKE,
    COMMENT
}
