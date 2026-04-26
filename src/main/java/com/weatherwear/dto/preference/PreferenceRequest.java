package com.weatherwear.dto.preference;

import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferenceRequest {

    @NotNull(message = "Style preference is required")
    private StylePreference stylePreference;

    @NotNull(message = "Cold sensitivity is required")
    private SensitivityLevel coldSensitivity;

    @NotNull(message = "Heat sensitivity is required")
    private SensitivityLevel heatSensitivity;

    private String preferredColors;

    private String avoidItems;
}