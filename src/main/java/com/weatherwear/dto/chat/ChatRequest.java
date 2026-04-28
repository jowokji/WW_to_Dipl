package com.weatherwear.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {

    private Long sessionId;

    @NotBlank(message = "Message is required")
    @Size(max = 3000, message = "Message must be at most 3000 characters")
    private String message;

    @Size(max = 120, message = "City must be at most 120 characters")
    private String city;
}
