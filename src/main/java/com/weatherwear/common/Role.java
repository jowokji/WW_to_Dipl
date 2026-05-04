package com.weatherwear.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application role assigned to a user.")
public enum Role {
    USER,
    ADMIN
}
