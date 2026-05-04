package com.weatherwear.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Chat session summary.")
public class ChatSessionResponse {

    @Schema(description = "Chat session identifier.", example = "12")
    private Long id;

    @Schema(description = "Chat session title.", example = "Style chat")
    private String title;

    @Schema(description = "Timestamp when the session was created.", example = "2026-04-30T12:40:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the session was last updated.", example = "2026-04-30T12:45:00")
    private LocalDateTime updatedAt;
}
