package com.weatherwear.repository;

import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {

    List<RecommendationHistory> findByUserOrderByCreatedAtDesc(User user);

    Optional<RecommendationHistory> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}
