package com.weatherwear.controller;

import com.weatherwear.entity.User;
import com.weatherwear.security.SecurityUtils;
import com.weatherwear.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        User user = securityUtils.getCurrentUser();

        userService.deleteAccount(user);
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}
