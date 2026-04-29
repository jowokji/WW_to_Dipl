package com.weatherwear.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherwear.common.ChatRole;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatRequest;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ChatController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @Test
    void sendMessage_returnsOk() throws Exception {
        when(chatService.sendMessage(any(ChatRequest.class)))
                .thenReturn(new ChatResponse(
                        1L,
                        "Wear a jacket",
                        LocalDateTime.of(2026, 4, 29, 12, 0)
                ));

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(1L))
                .andExpect(jsonPath("$.answer").value("Wear a jacket"));
    }

    @Test
    void sendMessage_blankMessage_returnsBadRequest() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setMessage("");

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSessionMessages_returnsOk() throws Exception {
        when(chatService.getSessionMessages(1L)).thenReturn(List.of(
                new ChatMessageDto(
                        10L,
                        ChatRole.USER,
                        "Hi",
                        LocalDateTime.of(2026, 4, 29, 12, 0)
                )
        ));

        mockMvc.perform(get("/chat/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    void getSessionMessages_notFound_returnsNotFound() throws Exception {
        when(chatService.getSessionMessages(99L))
                .thenThrow(new ResourceNotFoundException("Chat session not found"));

        mockMvc.perform(get("/chat/sessions/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSession_unauthorized_returnsUnauthorized() throws Exception {
        doThrow(new UnauthorizedException())
                .when(chatService)
                .deleteSession(1L);

        mockMvc.perform(delete("/chat/sessions/1"))
                .andExpect(status().isUnauthorized());
    }

    private ChatRequest request() {
        ChatRequest request = new ChatRequest();
        request.setMessage("What should I wear?");
        request.setCity("Vilnius");
        return request;
    }
}
