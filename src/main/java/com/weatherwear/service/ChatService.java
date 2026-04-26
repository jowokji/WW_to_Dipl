package com.weatherwear.service;

import com.weatherwear.client.llm.LlmClient;
import com.weatherwear.common.ChatRole;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatRequest;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.chat.ChatSessionResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.ChatMessage;
import com.weatherwear.entity.ChatSession;
import com.weatherwear.entity.User;
import com.weatherwear.repository.ChatMessageRepository;
import com.weatherwear.repository.ChatSessionRepository;
import com.weatherwear.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final SecurityUtils securityUtils;
    private final LlmClient llmClient;
    private final WeatherService weatherService;

    public ChatResponse sendMessage(ChatRequest request) {
        User user = securityUtils.getCurrentUser();

        ChatSession session = getOrCreateSession(request, user);

        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .role(ChatRole.USER)
                .content(request.getMessage())
                .build();

        messageRepository.save(userMessage);

        String prompt = buildChatPrompt(session, request);

        String answer = llmClient.generateRecommendation(prompt);

        ChatMessage assistantMessage = ChatMessage.builder()
                .session(session)
                .role(ChatRole.ASSISTANT)
                .content(answer)
                .build();

        ChatMessage savedAssistantMessage = messageRepository.save(assistantMessage);

        session.setUpdatedAt(savedAssistantMessage.getCreatedAt());
        sessionRepository.save(session);

        return new ChatResponse(
                session.getId(),
                answer,
                savedAssistantMessage.getCreatedAt()
        );
    }

    public List<ChatSessionResponse> getSessions() {
        User user = securityUtils.getCurrentUser();

        return sessionRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(session -> new ChatSessionResponse(
                        session.getId(),
                        session.getTitle(),
                        session.getCreatedAt(),
                        session.getUpdatedAt()
                ))
                .toList();
    }

    public List<ChatMessageDto> getSessionMessages(Long sessionId) {
        User user = securityUtils.getCurrentUser();

        ChatSession session = sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        return messageRepository.findBySessionOrderByCreatedAtAsc(session)
                .stream()
                .map(message -> new ChatMessageDto(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .toList();
    }

    public void deleteSession(Long sessionId) {
        User user = securityUtils.getCurrentUser();

        ChatSession session = sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        sessionRepository.delete(session);
    }

    private ChatSession getOrCreateSession(ChatRequest request, User user) {
        if (request.getSessionId() != null) {
            return sessionRepository.findByIdAndUser(request.getSessionId(), user)
                    .orElseThrow(() -> new RuntimeException("Chat session not found"));
        }

        ChatSession session = ChatSession.builder()
                .user(user)
                .title("Style chat")
                .build();

        return sessionRepository.save(session);
    }

    private String buildChatPrompt(ChatSession session, ChatRequest request) {
        String weatherContext = "";

        if (request.getCity() != null && !request.getCity().isBlank()) {
            WeatherResponse weather = weatherService.getWeatherByCity(request.getCity());

            weatherContext = """
                    Current weather:
                    City: %s
                    Temperature: %s°C
                    Feels like: %s°C
                    Wind: %s m/s
                    Condition: %s
                    Humidity: %s%%
                    """.formatted(
                    weather.getCity(),
                    weather.getTemperature(),
                    weather.getFeelsLike(),
                    weather.getWindSpeed(),
                    weather.getCondition(),
                    weather.getHumidity()
            );
        }

        List<ChatMessage> recentMessages = messageRepository
                .findTop10BySessionOrderByCreatedAtDesc(session)
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .toList();

        StringBuilder context = new StringBuilder();

        for (ChatMessage message : recentMessages) {
            context.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        return """
                You are an AI assistant in a mobile app called WeatherWear.
                You help users choose clothes based on weather, comfort and style.
                Answer clearly and practically.

                %s

                Conversation context:
                %s

                User question:
                %s
                """.formatted(
                weatherContext,
                context,
                request.getMessage()
        );
    }
}