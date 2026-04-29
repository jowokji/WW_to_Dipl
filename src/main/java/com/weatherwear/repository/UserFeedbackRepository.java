package com.weatherwear.repository;

import com.weatherwear.entity.RecommendationHistory;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    List<UserFeedback> findByUserOrderByCreatedAtDesc(User user);

    List<UserFeedback> findByRecommendationHistoryAndUserOrderByCreatedAtDesc(
            RecommendationHistory recommendationHistory,
            User user
    );

    Optional<UserFeedback> findByIdAndUser(Long id, User user);
}
