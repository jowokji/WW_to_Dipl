package com.weatherwear.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatResponse {

    private Long sessionId;
    private String answer;
    private LocalDateTime createdAt;
}
