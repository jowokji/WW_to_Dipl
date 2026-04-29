package com.weatherwear.repository;

import com.weatherwear.entity.WeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, Long> {

    Optional<WeatherCache> findTopByCityIgnoreCaseOrderByCachedAtDesc(String city);

    Optional<WeatherCache> findTopByLatitudeAndLongitudeOrderByCachedAtDesc(
            Double latitude,
            Double longitude
    );
}
