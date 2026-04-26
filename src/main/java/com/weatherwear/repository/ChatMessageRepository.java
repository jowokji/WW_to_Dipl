package com.weatherwear.repository;

import com.weatherwear.entity.ChatMessage;
import com.weatherwear.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);

    List<ChatMessage> findTop10BySessionOrderByCreatedAtDesc(ChatSession session);
}