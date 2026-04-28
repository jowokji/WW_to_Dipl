package com.weatherwear.dto.preference;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreferenceResponse {

    private Long id;
    private StylePreference stylePreference;
    private SensitivityLevel coldSensitivity;
    private SensitivityLevel heatSensitivity;
    private SensitivityLevel windSensitivity;
    private SensitivityLevel rainSensitivity;
    private Short maxLayers;
    private Boolean prefersHeadwear;
    private Boolean prefersWaterproof;
    private ActivityLevel activityLevel;
    private String preferredColors;
    private String avoidItems;
}
