package com.weatherwear.mapper;

import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.entity.UserPreference;
import org.springframework.stereotype.Component;

@Component
public class PreferenceMapper {

    public PreferenceResponse toResponse(UserPreference preference) {
        return new PreferenceResponse(
                preference.getId(),
                preference.getStylePreference(),
                preference.getColdSensitivity(),
                preference.getHeatSensitivity(),
                preference.getPreferredColors(),
                preference.getAvoidItems()
        );
    }
}