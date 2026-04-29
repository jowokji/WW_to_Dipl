package com.weatherwear.security;

import com.weatherwear.common.Role;
import com.weatherwear.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "jwtSecret",
                "this-is-a-very-long-secret-key-for-weatherwear-tests"
        );
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
    }

    @Test
    void generateToken_extractEmail_success() {
        String token = jwtService.generateToken(user("user@example.com"));

        assertThat(jwtService.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void isTokenValid_returnsTrueForSameUser() {
        User user = user("user@example.com");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForDifferentUser() {
        String token = jwtService.generateToken(user("user@example.com"));

        assertThat(jwtService.isTokenValid(token, user("other@example.com"))).isFalse();
    }

    @Test
    void extractEmail_invalidToken_throwsJwtException() {
        assertThatThrownBy(() -> jwtService.extractEmail("not-a-token"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void isTokenValid_expiredToken_throwsJwtException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1_000L);
        String token = jwtService.generateToken(user("user@example.com"));

        assertThatThrownBy(() -> jwtService.isTokenValid(token, user("user@example.com")))
                .isInstanceOf(JwtException.class);
    }

    private User user(String email) {
        return User.builder()
                .id(1L)
                .email(email)
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }
}
