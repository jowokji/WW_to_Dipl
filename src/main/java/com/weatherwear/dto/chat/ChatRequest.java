package com.weatherwear.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {

    private Long sessionId;
    private String message;
    private String city;
}