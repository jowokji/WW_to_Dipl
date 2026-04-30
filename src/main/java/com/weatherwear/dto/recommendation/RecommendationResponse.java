package com.weatherwear.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendationResponse {

    private String city;
    private String weatherSummary;
    private String recommendation;
}
