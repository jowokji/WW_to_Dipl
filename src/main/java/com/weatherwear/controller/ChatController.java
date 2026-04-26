package com.weatherwear.controller;

import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatRequest;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.chat.ChatSessionResponse;
import com.weatherwear.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse sendMessage(@RequestBody ChatRequest request) {
        return chatService.sendMessage(request);
    }

    @GetMapping("/sessions")
    public List<ChatSessionResponse> getSessions() {
        return chatService.getSessions();
    }

    @GetMapping("/sessions/{id}")
    public List<ChatMessageDto> getSessionMessages(@PathVariable Long id) {
        return chatService.getSessionMessages(id);
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}