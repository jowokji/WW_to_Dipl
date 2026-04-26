package com.weatherwear.dto.recommendation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationRequest {

    private String city;

    private Double latitude;
    private Double longitude;

    private String occasion; // work / walk / date / sport
}