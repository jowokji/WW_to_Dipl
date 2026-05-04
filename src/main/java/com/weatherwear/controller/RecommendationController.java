package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import com.weatherwear.dto.error.ErrorResponse;
import com.weatherwear.dto.recommendation.RecommendationRequest;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Recommendations", description = "AI clothing recommendation workflow.")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    @Operation(
            summary = "Generate a clothing recommendation",
            description = "Generates an AI clothing recommendation from weather, optional occasion, "
                    + "and the authenticated user's saved preferences. The result is stored in history.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RecommendationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "city",
                                            summary = "Recommendation by city",
                                            value = OpenApiExamples.RECOMMENDATION_REQUEST_CITY
                                    ),
                                    @ExampleObject(
                                            name = "coordinates",
                                            summary = "Recommendation by coordinates",
                                            value = OpenApiExamples.RECOMMENDATION_REQUEST_COORDINATES
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Generated recommendation.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RecommendationResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.RECOMMENDATION_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Location is missing or request validation failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is missing, invalid, or expired.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.UNAUTHORIZED_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "OpenWeather or LLM dependency failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.BAD_GATEWAY_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.SERVER_ERROR)
                    )
            )
    })
    public RecommendationResponse getRecommendation(
            @Valid @RequestBody RecommendationRequest request
    ) {
        return recommendationService.getRecommendation(request);
    }
}
