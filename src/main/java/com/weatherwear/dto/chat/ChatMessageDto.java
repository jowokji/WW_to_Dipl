package com.weatherwear.dto.chat;

import com.weatherwear.common.ChatRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;
    private ChatRole role;
    private String content;
    private LocalDateTime createdAt;
}
