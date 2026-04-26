package com.weatherwear.dto.preference;

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
    private String preferredColors;
    private String avoidItems;
}