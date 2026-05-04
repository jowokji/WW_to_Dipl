package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import com.weatherwear.dto.error.ErrorResponse;
import com.weatherwear.dto.feedback.FeedbackRequest;
import com.weatherwear.dto.feedback.FeedbackResponse;
import com.weatherwear.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Feedback", description = "User feedback for generated recommendations.")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(
            summary = "Create feedback for a recommendation",
            description = "Adds feedback to one recommendation history item owned by the current user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FeedbackRequest.class),
                            examples = @ExampleObject(value = OpenApiExamples.FEEDBACK_REQUEST)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback created.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FeedbackResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.FEEDBACK_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed.",
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
                    responseCode = "404",
                    description = "Recommendation history item was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
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
    public FeedbackResponse createFeedback(
            @Valid @RequestBody FeedbackRequest request
    ) {
        return feedbackService.createFeedback(request);
    }

    @GetMapping
    @Operation(
            summary = "List current user's feedback",
            description = "Returns all feedback rows created by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback list.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FeedbackResponse.class)),
                            examples = @ExampleObject(value = OpenApiExamples.FEEDBACK_LIST_RESPONSE)
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
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.SERVER_ERROR)
                    )
            )
    })
    public List<FeedbackResponse> getFeedback() {
        return feedbackService.getCurrentUserFeedback();
    }

    @GetMapping("/recommendations/{recommendationHistoryId}")
    @Operation(
            summary = "List feedback for one recommendation",
            description = "Returns feedback for a recommendation history item owned by the current user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback list for the recommendation.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FeedbackResponse.class)),
                            examples = @ExampleObject(value = OpenApiExamples.FEEDBACK_LIST_RESPONSE)
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
                    responseCode = "404",
                    description = "Recommendation history item was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
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
    public List<FeedbackResponse> getRecommendationFeedback(
            @Parameter(
                    description = "Recommendation history identifier.",
                    required = true,
                    example = "101"
            )
            @PathVariable Long recommendationHistoryId
    ) {
        return feedbackService.getCurrentUserFeedbackForRecommendation(
                recommendationHistoryId
        );
    }

    @DeleteMapping("/{feedbackId}")
    @Operation(
            summary = "Delete feedback",
            description = "Deletes one feedback row owned by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feedback deleted successfully."),
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
                    responseCode = "404",
                    description = "Feedback row was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
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
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(
                    description = "Feedback identifier.",
                    required = true,
                    example = "501"
            )
            @PathVariable Long feedbackId
    ) {
        feedbackService.deleteCurrentUserFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
