package com.weatherwear.mapper;

import com.weatherwear.common.ChatRole;
import com.weatherwear.dto.chat.ChatMessageDto;
import com.weatherwear.dto.chat.ChatResponse;
import com.weatherwear.dto.chat.ChatSessionResponse;
import com.weatherwear.entity.ChatMessage;
import com.weatherwear.entity.ChatSession;
import com.weatherwear.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatMessageDto toMessageDto(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    public ChatSessionResponse toSessionResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public ChatResponse toChatResponse(ChatSession session, ChatMessage assistantMessage) {
        return new ChatResponse(
                session.getId(),
                assistantMessage.getContent(),
                assistantMessage.getCreatedAt()
        );
    }

    public ChatSession toSession(User user, String title) {
        return ChatSession.builder()
                .user(user)
                .title(title)
                .build();
    }

    public ChatMessage toUserMessage(ChatSession session, String content) {
        return toMessage(session, ChatRole.USER, content);
    }

    public ChatMessage toAssistantMessage(ChatSession session, String content) {
        return toMessage(session, ChatRole.ASSISTANT, content);
    }

    private ChatMessage toMessage(ChatSession session, ChatRole role, String content) {
        return ChatMessage.builder()
                .session(session)
                .role(role)
                .content(content)
                .build();
    }
}
