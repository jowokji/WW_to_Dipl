package com.weatherwear.dto.preference;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    private SensitivityLevel windSensitivity;

    private SensitivityLevel rainSensitivity;

    @Min(value = 1, message = "Max layers must be at least 1")
    @Max(value = 5, message = "Max layers must be at most 5")
    private Short maxLayers;

    private Boolean prefersHeadwear;

    private Boolean prefersWaterproof;

    private ActivityLevel activityLevel;

    private String preferredColors;

    private String avoidItems;
}
