package com.weatherwear.entity;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "wind_sensitivity", nullable = false)
    private SensitivityLevel windSensitivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "rain_sensitivity", nullable = false)
    private SensitivityLevel rainSensitivity;

    @Column(name = "max_layers", nullable = false)
    private Short maxLayers;

    @Column(name = "prefers_headwear", nullable = false)
    private Boolean prefersHeadwear;

    @Column(name = "prefers_waterproof", nullable = false)
    private Boolean prefersWaterproof;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevel activityLevel;

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
        applyDefaults();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        applyDefaults();
    }

    private void applyDefaults() {
        if (this.stylePreference == null) {
            this.stylePreference = StylePreference.CASUAL;
        }

        if (this.coldSensitivity == null) {
            this.coldSensitivity = SensitivityLevel.MEDIUM;
        }

        if (this.heatSensitivity == null) {
            this.heatSensitivity = SensitivityLevel.MEDIUM;
        }

        if (this.windSensitivity == null) {
            this.windSensitivity = SensitivityLevel.MEDIUM;
        }

        if (this.rainSensitivity == null) {
            this.rainSensitivity = SensitivityLevel.MEDIUM;
        }

        if (this.maxLayers == null) {
            this.maxLayers = 3;
        }

        if (this.prefersHeadwear == null) {
            this.prefersHeadwear = false;
        }

        if (this.prefersWaterproof == null) {
            this.prefersWaterproof = false;
        }

        if (this.activityLevel == null) {
            this.activityLevel = ActivityLevel.MEDIUM;
        }
    }
}
