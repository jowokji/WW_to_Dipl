package com.weatherwear.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Chat request for starting or continuing an AI style assistant session.")
public class ChatRequest {

    @Schema(description = "Existing chat session ID. Omit to start a new session.", example = "12")
    private Long sessionId;

    @NotBlank(message = "Message is required")
    @Size(max = 3000, message = "Message must be at most 3000 characters")
    @Schema(
            description = "User message for the style assistant.",
            example = "What should I wear for a long walk today?",
            maxLength = 3000
    )
    private String message;

    @Size(max = 120, message = "City must be at most 120 characters")
    @Schema(description = "Optional city used to add current weather context to the prompt.",
            example = "Vilnius", maxLength = 120)
    private String city;
}
