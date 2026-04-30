package com.weatherwear.controller;

import com.weatherwear.dto.recommendation.RecommendationRequest;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationResponse getRecommendation(
            @Valid @RequestBody RecommendationRequest request
    ) {
        return recommendationService.getRecommendation(request);
    }
}
