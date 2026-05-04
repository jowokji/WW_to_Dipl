package com.weatherwear.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "AI clothing recommendation generated for the requested location and context.")
public class RecommendationResponse {

    @Schema(description = "City used for weather lookup.", example = "Vilnius")
    private String city;

    @Schema(description = "Compact weather summary used in the recommendation prompt.",
            example = "Temp: 12.4, Feels: 10.8, Condition: CLOUDS")
    private String weatherSummary;

    @Schema(description = "Generated clothing recommendation text.",
            example = "Wear a light waterproof jacket, jeans, and closed shoes.")
    private String recommendation;
}
