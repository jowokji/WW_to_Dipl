package com.weatherwear.dto.chat;

import com.weatherwear.common.ChatRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "One message inside a chat session.")
public class ChatMessageDto {

    @Schema(description = "Chat message identifier.", example = "9001")
    private Long id;

    @Schema(description = "Message author role.", example = "USER")
    private ChatRole role;

    @Schema(description = "Message text.", example = "What should I wear for a long walk today?")
    private String content;

    @Schema(description = "Timestamp when the message was created.", example = "2026-04-30T12:45:00")
    private LocalDateTime createdAt;
}
