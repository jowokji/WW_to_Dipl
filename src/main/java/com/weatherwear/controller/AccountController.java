package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import com.weatherwear.dto.error.ErrorResponse;
import com.weatherwear.entity.User;
import com.weatherwear.security.SecurityUtils;
import com.weatherwear.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Account", description = "Authenticated account lifecycle endpoints.")
public class AccountController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @DeleteMapping("/me")
    @Operation(
            summary = "Delete current user account",
            description = "Deletes the authenticated user account. Database foreign keys cascade "
                    + "the user's preferences, history, feedback, chat sessions, and chat messages."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is missing, invalid, or expired.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.UNAUTHORIZED_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.SERVER_ERROR)
                    )
            )
    })
    public ResponseEntity<Void> deleteCurrentUser() {
        User user = securityUtils.getCurrentUser();

        userService.deleteAccount(user);
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}
