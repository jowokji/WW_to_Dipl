package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Role of a message inside a chat session.")
public enum ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}
