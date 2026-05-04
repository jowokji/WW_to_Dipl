package com.weatherwear.dto.preference;

import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Persisted user preference profile.")
public class PreferenceResponse {

    @Schema(description = "Preference row identifier.", example = "7")
    private Long id;

    @Schema(description = "Preferred clothing style.", example = "CASUAL")
    private StylePreference stylePreference;

    @Schema(description = "How strongly cold weather affects the user.", example = "MEDIUM")
    private SensitivityLevel coldSensitivity;

    @Schema(description = "How strongly hot weather affects the user.", example = "LOW")
    private SensitivityLevel heatSensitivity;

    @Schema(description = "How strongly wind affects the user.", example = "HIGH")
    private SensitivityLevel windSensitivity;

    @Schema(description = "How strongly rain affects the user.", example = "MEDIUM")
    private SensitivityLevel rainSensitivity;

    @Schema(description = "Maximum acceptable clothing layers.", example = "3", minimum = "1", maximum = "5")
    private Short maxLayers;

    @Schema(description = "Whether the user prefers hats, caps, or other headwear.", example = "true")
    private Boolean prefersHeadwear;

    @Schema(description = "Whether waterproof items should be prioritized in wet weather.", example = "true")
    private Boolean prefersWaterproof;

    @Schema(description = "Expected activity intensity.", example = "MEDIUM")
    private ActivityLevel activityLevel;

    @Schema(description = "Comma-separated preferred colors.", example = "black, navy, grey")
    private String preferredColors;

    @Schema(description = "Comma-separated clothing items or materials to avoid.", example = "wool sweaters")
    private String avoidItems;
}
