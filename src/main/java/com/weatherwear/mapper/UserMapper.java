package com.weatherwear.mapper;

import com.weatherwear.dto.auth.AuthResponse;
import com.weatherwear.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}
