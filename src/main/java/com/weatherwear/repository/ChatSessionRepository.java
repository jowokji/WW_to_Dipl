package com.weatherwear.repository;

import com.weatherwear.entity.ChatSession;
import com.weatherwear.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);

    Optional<ChatSession> findByIdAndUser(Long id, User user);
}
