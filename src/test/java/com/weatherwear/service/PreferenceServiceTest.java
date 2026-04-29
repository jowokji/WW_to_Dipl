package com.weatherwear.service;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.Role;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.dto.preference.PreferenceRequest;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.entity.User;
import com.weatherwear.entity.UserPreference;
import com.weatherwear.exception.UserNotFoundException;
import com.weatherwear.repository.UserPreferenceRepository;
import com.weatherwear.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @Mock
    private UserPreferenceRepository preferenceRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PreferenceService preferenceService;

    @Test
    void getPreferences_success() {
        User user = user();
        UserPreference preference = preference(user);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));

        PreferenceResponse response = preferenceService.getCurrentUserPreferences();

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getStylePreference()).isEqualTo(StylePreference.CASUAL);
        assertThat(response.getColdSensitivity()).isEqualTo(SensitivityLevel.MEDIUM);
        verify(preferenceRepository, never()).save(any(UserPreference.class));
    }

    @Test
    void getPreferences_userNotFound() {
        when(securityUtils.getCurrentUser())
                .thenThrow(new UserNotFoundException("missing@example.com"));

        assertThatThrownBy(() -> preferenceService.getCurrentUserPreferences())
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(preferenceRepository);
    }

    @Test
    void createPreferences_success() {
        User user = user();
        PreferenceRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(preferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> {
            UserPreference saved = invocation.getArgument(0);
            saved.setId(30L);
            return saved;
        });

        PreferenceResponse response = preferenceService.createCurrentUserPreferences(request);

        assertThat(response.getId()).isEqualTo(30L);
        assertThat(response.getStylePreference()).isEqualTo(StylePreference.BUSINESS);
        assertThat(response.getColdSensitivity()).isEqualTo(SensitivityLevel.HIGH);
        assertThat(response.getActivityLevel()).isEqualTo(ActivityLevel.HIGH);

        ArgumentCaptor<UserPreference> preferenceCaptor =
                ArgumentCaptor.forClass(UserPreference.class);
        verify(preferenceRepository).save(preferenceCaptor.capture());
        assertThat(preferenceCaptor.getValue().getUser()).isSameAs(user);
    }

    @Test
    void updatePreferences_success() {
        User user = user();
        UserPreference existingPreference = preference(user);
        PreferenceRequest request = request();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(existingPreference)).thenReturn(existingPreference);

        PreferenceResponse response = preferenceService.updateCurrentUserPreferences(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getStylePreference()).isEqualTo(StylePreference.BUSINESS);
        assertThat(response.getColdSensitivity()).isEqualTo(SensitivityLevel.HIGH);
        assertThat(response.getHeatSensitivity()).isEqualTo(SensitivityLevel.LOW);
        assertThat(response.getMaxLayers()).isEqualTo((short) 4);
        assertThat(response.getPrefersWaterproof()).isTrue();
    }

    @Test
    void updatePreferences_userNotFound() {
        when(securityUtils.getCurrentUser())
                .thenThrow(new UserNotFoundException("missing@example.com"));

        assertThatThrownBy(() -> preferenceService.updateCurrentUserPreferences(request()))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(preferenceRepository);
    }

    private User user() {
        return User.builder()
                .id(1L)
                .email("user@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    private UserPreference preference(User user) {
        return UserPreference.builder()
                .id(10L)
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
                .preferredColors("black")
                .avoidItems("sandals")
                .build();
    }

    private PreferenceRequest request() {
        PreferenceRequest request = new PreferenceRequest();
        request.setStylePreference(StylePreference.BUSINESS);
        request.setColdSensitivity(SensitivityLevel.HIGH);
        request.setHeatSensitivity(SensitivityLevel.LOW);
        request.setWindSensitivity(SensitivityLevel.HIGH);
        request.setRainSensitivity(SensitivityLevel.HIGH);
        request.setMaxLayers((short) 4);
        request.setPrefersHeadwear(true);
        request.setPrefersWaterproof(true);
        request.setActivityLevel(ActivityLevel.HIGH);
        request.setPreferredColors("navy");
        request.setAvoidItems("shorts");
        return request;
    }
}
