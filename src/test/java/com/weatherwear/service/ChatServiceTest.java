package com.weatherwear.service;

import com.weatherwear.client.llm.LlmClient;
import com.weatherwear.common.ChatRole;
import com.weatherwear.common.Role;
import com.weatherwear.common.WeatherCondition;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatRequest;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.weather.WeatherResponse;
import com.weatherwear.entity.ChatMessage;
import com.weatherwear.entity.ChatSession;
import com.weatherwear.entity.User;
import com.weatherwear.exception.LlmApiException;
import com.weatherwear.repository.ChatMessageRepository;
import com.weatherwear.repository.ChatSessionRepository;
import com.weatherwear.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private LlmClient llmClient;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void sendMessage_success() {
        User user = user();
        ChatRequest request = request();
        LocalDateTime assistantCreatedAt = LocalDateTime.of(2026, 4, 29, 12, 0);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession session = invocation.getArgument(0);
            if (session.getId() == null) {
                session.setId(1L);
            }
            return session;
        });
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weatherResponse());
        when(messageRepository.findTop10BySessionOrderByCreatedAtDesc(any(ChatSession.class)))
                .thenReturn(List.of());
        when(llmClient.generateRecommendation(any(String.class))).thenReturn("Wear a jacket");
        when(messageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            message.setId(message.getRole() == ChatRole.USER ? 10L : 11L);
            message.setCreatedAt(message.getRole() == ChatRole.USER
                    ? assistantCreatedAt.minusMinutes(1)
                    : assistantCreatedAt);
            return message;
        });

        ChatResponse response = chatService.sendMessage(request);

        assertThat(response.getSessionId()).isEqualTo(1L);
        assertThat(response.getAnswer()).isEqualTo("Wear a jacket");
        assertThat(response.getCreatedAt()).isEqualTo(assistantCreatedAt);
    }

    @Test
    void sendMessage_savesChatMessage() {
        User user = user();
        ChatRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession session = invocation.getArgument(0);
            session.setId(1L);
            return session;
        });
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weatherResponse());
        when(messageRepository.findTop10BySessionOrderByCreatedAtDesc(any(ChatSession.class)))
                .thenReturn(List.of());
        when(llmClient.generateRecommendation(any(String.class))).thenReturn("Wear a jacket");
        when(messageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            message.setCreatedAt(LocalDateTime.of(2026, 4, 29, 12, 0));
            return message;
        });

        chatService.sendMessage(request);

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(messageRepository, org.mockito.Mockito.times(2)).save(messageCaptor.capture());

        List<ChatMessage> savedMessages = messageCaptor.getAllValues();
        assertThat(savedMessages.get(0).getRole()).isEqualTo(ChatRole.USER);
        assertThat(savedMessages.get(0).getContent()).isEqualTo("What should I wear?");
        assertThat(savedMessages.get(1).getRole()).isEqualTo(ChatRole.ASSISTANT);
        assertThat(savedMessages.get(1).getContent()).isEqualTo("Wear a jacket");
    }

    @Test
    void getChatHistory_success() {
        User user = user();
        ChatSession session = session(user);
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 29, 12, 0);

        ChatMessage userMessage = ChatMessage.builder()
                .id(10L)
                .session(session)
                .role(ChatRole.USER)
                .content("Hi")
                .createdAt(createdAt)
                .build();

        ChatMessage assistantMessage = ChatMessage.builder()
                .id(11L)
                .session(session)
                .role(ChatRole.ASSISTANT)
                .content("Hello")
                .createdAt(createdAt.plusSeconds(10))
                .build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session))
                .thenReturn(List.of(userMessage, assistantMessage));

        List<ChatMessageDto> history = chatService.getSessionMessages(1L);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getRole()).isEqualTo(ChatRole.USER);
        assertThat(history.get(0).getContent()).isEqualTo("Hi");
        assertThat(history.get(1).getRole()).isEqualTo(ChatRole.ASSISTANT);
        assertThat(history.get(1).getContent()).isEqualTo("Hello");
    }

    @Test
    void clearChatHistory_success() {
        User user = user();
        ChatSession session = session(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(session));

        chatService.deleteSession(1L);

        verify(sessionRepository).delete(session);
    }

    @Test
    void llmError_throwsException() {
        User user = user();
        ChatRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession session = invocation.getArgument(0);
            session.setId(1L);
            return session;
        });
        when(weatherService.getWeatherByCity("Vilnius")).thenReturn(weatherResponse());
        when(messageRepository.findTop10BySessionOrderByCreatedAtDesc(any(ChatSession.class)))
                .thenReturn(List.of());
        when(messageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            message.setCreatedAt(LocalDateTime.of(2026, 4, 29, 12, 0));
            return message;
        });
        when(llmClient.generateRecommendation(any(String.class)))
                .thenThrow(new LlmApiException("LLM failed"));

        assertThatThrownBy(() -> chatService.sendMessage(request))
                .isInstanceOf(LlmApiException.class);
    }

    private ChatRequest request() {
        ChatRequest request = new ChatRequest();
        request.setMessage("What should I wear?");
        request.setCity("Vilnius");
        return request;
    }

    private User user() {
        return User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    private ChatSession session(User user) {
        return ChatSession.builder()
                .id(1L)
                .user(user)
                .title("Style chat")
                .createdAt(LocalDateTime.of(2026, 4, 29, 11, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 29, 11, 0))
                .build();
    }

    private WeatherResponse weatherResponse() {
        return new WeatherResponse(
                "Vilnius",
                54.6872,
                25.2797,
                12.0,
                10.0,
                70,
                4.5,
                WeatherCondition.CLOUDS,
                0.0,
                false
        );
    }
}
