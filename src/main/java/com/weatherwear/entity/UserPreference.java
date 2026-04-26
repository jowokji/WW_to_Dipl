package com.weatherwear.entity;

import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one user = one preferences record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "style_preference", nullable = false)
    private StylePreference stylePreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "cold_sensitivity", nullable = false)
    private SensitivityLevel coldSensitivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "heat_sensitivity", nullable = false)
    private SensitivityLevel heatSensitivity;

    @Column(name = "preferred_colors")
    private String preferredColors;

    @Column(name = "avoid_items")
    private String avoidItems;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.stylePreference == null) {
            this.stylePreference = StylePreference.CASUAL;
        }

        if (this.coldSensitivity == null) {
            this.coldSensitivity = SensitivityLevel.MEDIUM;
        }

        if (this.heatSensitivity == null) {
            this.heatSensitivity = SensitivityLevel.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}