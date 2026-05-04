package com.weatherwear.controller;

import com.weatherwear.common.Role;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.entity.User;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.security.SecurityUtils;
import com.weatherwear.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityUtils securityUtils;

    @Test
    void deleteCurrentUser_returnsNoContent() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(delete("/users/me"))
                .andExpect(status().isNoContent());

        verify(userService).deleteAccount(user);
    }
}
