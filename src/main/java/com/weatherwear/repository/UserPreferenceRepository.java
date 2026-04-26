package com.weatherwear.repository;

import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUser(User user);

    boolean existsByUser(User user);
}