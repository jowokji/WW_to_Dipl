package com.weatherwear.service;

import com.weatherwear.common.ActivityLevel;
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

    public PreferenceResponse createCurrentUserPreferences(PreferenceRequest request) {
        User user = securityUtils.getCurrentUser();

        UserPreference preference = UserPreference.builder()
                .user(user)
                .build();

        applyRequest(preference, request);

        return toResponse(preferenceRepository.save(preference));
    }

    public PreferenceResponse updateCurrentUserPreferences(PreferenceRequest request) {
        User user = securityUtils.getCurrentUser();

        UserPreference preference = preferenceRepository.findByUser(user)
                .orElseGet(() -> createDefaultPreferences(user));

        applyRequest(preference, request);

        UserPreference savedPreference = preferenceRepository.save(preference);

        return toResponse(savedPreference);
    }

    private void applyRequest(UserPreference preference, PreferenceRequest request) {
        preference.setStylePreference(request.getStylePreference());
        preference.setColdSensitivity(request.getColdSensitivity());
        preference.setHeatSensitivity(request.getHeatSensitivity());
        preference.setWindSensitivity(valueOrDefault(
                request.getWindSensitivity(),
                preference.getWindSensitivity(),
                SensitivityLevel.MEDIUM
        ));
        preference.setRainSensitivity(valueOrDefault(
                request.getRainSensitivity(),
                preference.getRainSensitivity(),
                SensitivityLevel.MEDIUM
        ));
        preference.setMaxLayers(valueOrDefault(
                request.getMaxLayers(),
                preference.getMaxLayers(),
                (short) 3
        ));
        preference.setPrefersHeadwear(valueOrDefault(
                request.getPrefersHeadwear(),
                preference.getPrefersHeadwear(),
                false
        ));
        preference.setPrefersWaterproof(valueOrDefault(
                request.getPrefersWaterproof(),
                preference.getPrefersWaterproof(),
                false
        ));
        preference.setActivityLevel(valueOrDefault(
                request.getActivityLevel(),
                preference.getActivityLevel(),
                ActivityLevel.MEDIUM
        ));
        preference.setPreferredColors(request.getPreferredColors());
        preference.setAvoidItems(request.getAvoidItems());
    }

    private UserPreference createDefaultPreferences(User user) {
        UserPreference preference = UserPreference.builder()
                .user(user)
                .stylePreference(StylePreference.CASUAL)
                .coldSensitivity(SensitivityLevel.MEDIUM)
                .heatSensitivity(SensitivityLevel.MEDIUM)
                .windSensitivity(SensitivityLevel.MEDIUM)
                .rainSensitivity(SensitivityLevel.MEDIUM)
                .maxLayers((short) 3)
                .prefersHeadwear(false)
                .prefersWaterproof(false)
                .activityLevel(ActivityLevel.MEDIUM)
                .preferredColors("")
                .avoidItems("")
                .build();

        return preferenceRepository.save(preference);
    }

    private <T> T valueOrDefault(T requestValue, T currentValue, T defaultValue) {
        if (requestValue != null) {
            return requestValue;
        }

        return currentValue != null ? currentValue : defaultValue;
    }

    private PreferenceResponse toResponse(UserPreference preference) {
        return new PreferenceResponse(
                preference.getId(),
                preference.getStylePreference(),
                preference.getColdSensitivity(),
                preference.getHeatSensitivity(),
                preference.getWindSensitivity(),
                preference.getRainSensitivity(),
                preference.getMaxLayers(),
                preference.getPrefersHeadwear(),
                preference.getPrefersWaterproof(),
                preference.getActivityLevel(),
                preference.getPreferredColors(),
                preference.getAvoidItems()
        );
    }
}
