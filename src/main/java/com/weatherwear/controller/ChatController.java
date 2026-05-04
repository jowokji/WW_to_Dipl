package com.weatherwear.controller;

import com.weatherwear.config.OpenApiExamples;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatRequest;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.chat.ChatSessionResponse;
import com.weatherwear.dto.error.ErrorResponse;
import com.weatherwear.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat", description = "Weather-aware conversational AI style assistant.")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(
            summary = "Send message to AI style assistant",
            description = "Creates or continues a chat session. When city is supplied, current weather "
                    + "is included in the assistant context.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "newSession",
                                            summary = "Start a session",
                                            value = OpenApiExamples.CHAT_REQUEST_NEW_SESSION
                                    ),
                                    @ExampleObject(
                                            name = "existingSession",
                                            summary = "Continue an existing session",
                                            value = OpenApiExamples.CHAT_REQUEST_EXISTING_SESSION
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assistant answer.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.CHAT_RESPONSE)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Message is missing, too long, or request validation failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.VALIDATION_ERROR)
                    )
            ),
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
                    responseCode = "404",
                    description = "Session was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Weather or LLM dependency failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.BAD_GATEWAY_ERROR)
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
    public ChatResponse sendMessage(@Valid @RequestBody ChatRequest request) {
        return chatService.sendMessage(request);
    }

    @GetMapping("/sessions")
    @Operation(
            summary = "List chat sessions",
            description = "Returns chat sessions owned by the current user, ordered by most recent update."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chat session list.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ChatSessionResponse.class)),
                            examples = @ExampleObject(value = OpenApiExamples.CHAT_SESSIONS_RESPONSE)
                    )
            ),
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
    public List<ChatSessionResponse> getSessions() {
        return chatService.getSessions();
    }

    @GetMapping("/sessions/{id}")
    @Operation(
            summary = "List messages in a chat session",
            description = "Returns messages for one chat session if it belongs to the current user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chat session messages.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ChatMessageDto.class)),
                            examples = @ExampleObject(value = OpenApiExamples.CHAT_MESSAGES_RESPONSE)
                    )
            ),
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
                    responseCode = "404",
                    description = "Session was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
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
    public List<ChatMessageDto> getSessionMessages(
            @Parameter(
                    description = "Chat session identifier.",
                    required = true,
                    example = "12"
            )
            @PathVariable Long id
    ) {
        return chatService.getSessionMessages(id);
    }

    @DeleteMapping("/sessions/{id}")
    @Operation(
            summary = "Delete chat session",
            description = "Deletes one chat session and its messages if it belongs to the current user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chat session deleted successfully."),
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
                    responseCode = "404",
                    description = "Session was not found or is not owned by the current user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = OpenApiExamples.NOT_FOUND_ERROR)
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
    public ResponseEntity<Void> deleteSession(
            @Parameter(
                    description = "Chat session identifier.",
                    required = true,
                    example = "12"
            )
            @PathVariable Long id
    ) {
        chatService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
