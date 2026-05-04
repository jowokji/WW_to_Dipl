package com.weatherwear.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Recommendation request. Provide either a city or both latitude and longitude.")
public class RecommendationRequest {

    @Size(max = 120, message = "City must be at most 120 characters")
    @Schema(description = "City name used for weather lookup.", example = "Vilnius", maxLength = 120)
    private String city;

    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
    @Schema(
            description = "Latitude in decimal degrees. Required when city is not provided.",
            example = "54.6872",
            minimum = "-90.0",
            maximum = "90.0"
    )
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
    @Schema(
            description = "Longitude in decimal degrees. Required when city is not provided.",
            example = "25.2797",
            minimum = "-180.0",
            maximum = "180.0"
    )
    private Double longitude;

    @Schema(description = "Optional free-text context such as work, walk, date, or sport.", example = "work")
    private String occasion;

    @AssertTrue(message = "City or both latitude and longitude are required")
    @Schema(hidden = true)
    public boolean hasLocation() {
        return hasCity() || hasCoordinates();
    }

    private boolean hasCity() {
        return city != null && !city.isBlank();
    }

    private boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
