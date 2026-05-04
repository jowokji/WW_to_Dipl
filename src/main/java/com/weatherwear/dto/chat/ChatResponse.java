package com.weatherwear.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Assistant response returned after a chat message is processed.")
public class ChatResponse {

    @Schema(description = "Chat session identifier.", example = "12")
    private Long sessionId;

    @Schema(description = "AI assistant answer.",
            example = "Choose a breathable base layer, a light jacket, comfortable trousers, and shoes with grip.")
    private String answer;

    @Schema(description = "Timestamp when the assistant answer was created.", example = "2026-04-30T12:45:00")
    private LocalDateTime createdAt;
}
