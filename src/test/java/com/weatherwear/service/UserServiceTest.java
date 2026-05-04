package com.weatherwear.service;

import com.weatherwear.common.Role;
import com.weatherwear.entity.User;
import com.weatherwear.exception.UserNotFoundException;
import com.weatherwear.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getByEmail_success() {
        User user = user();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThat(userService.getByEmail("user@example.com")).isSameAs(user);
    }

    @Test
    void getByEmail_notFound_throwsException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByEmail("missing@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getById_success() {
        User user = user();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.getById(1L)).isSameAs(user);
    }

    @Test
    void getById_notFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with id 99 not found");
    }

    @Test
    void existsByEmail_delegatesToRepository() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThat(userService.existsByEmail("user@example.com")).isTrue();
    }

    @Test
    void save_delegatesToRepository() {
        User user = user();

        when(userRepository.save(user)).thenReturn(user);

        assertThat(userService.save(user)).isSameAs(user);
    }

    @Test
    void deleteAccount_delegatesToRepository() {
        User user = user();

        userService.deleteAccount(user);

        verify(userRepository).delete(user);
    }

    private User user() {
        return User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }
}
