package com.weatherwear.entity;

import com.weatherwear.common.WeatherCondition;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private Double latitude;

    private Double longitude;

    private Double temperature;

    private Double feelsLike;

    private Integer humidity;

    private Double windSpeed;

    @Enumerated(EnumType.STRING)
    private WeatherCondition condition;

    private Double precipitation;

    @Column(name = "cached_at", nullable = false)
    private LocalDateTime cachedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}