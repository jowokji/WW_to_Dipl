package com.weatherwear.service;

import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.dto.preference.PreferenceRequest;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import com.weatherwear.repository.UserPreferenceRepository;
import com.weatherwear.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final SecurityUtils securityUtils;

    public PreferenceResponse getCurrentUserPreferences() {
        User user = securityUtils.getCurrentUser();

        UserPreference preference = preferenceRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreferences(user));

        return toResponse(preference);
    }

    public PreferenceResponse updateCurrentUserPreferences(PreferenceRequest request) {
        User user = securityUtils.getCurrentUser();

        UserPreference preference = preferenceRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreferences(user));

        preference.setStylePreference(request.getStylePreference());
        preference.setColdSensitivity(request.getColdSensitivity());
        preference.setHeatSensitivity(request.getHeatSensitivity());
        preference.setPreferredColors(request.getPreferredColors());
        preference.setAvoidItems(request.getAvoidItems());

        UserPreference savedPreference = preferenceRepository.save(preference);

        return toResponse(savedPreference);
    }

    private UserPreference createDefaultPreferences(User user) {
        UserPreference preference = UserPreference.builder()
                .user(user)
                .stylePreference(StylePreference.CASUAL)
                .coldSensitivity(SensitivityLevel.MEDIUM)
                .heatSensitivity(SensitivityLevel.MEDIUM)
                .preferredColors("")
                .avoidItems("")
                .build();

        return preferenceRepository.save(preference);
    }

    private PreferenceResponse toResponse(UserPreference preference) {
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