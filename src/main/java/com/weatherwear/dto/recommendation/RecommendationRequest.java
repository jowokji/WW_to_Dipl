package com.weatherwear.dto.recommendation;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationRequest {

    @Size(max = 120, message = "City must be at most 120 characters")
    private String city;

    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
    private Double longitude;

    private String occasion; // work / walk / date / sport

    @AssertTrue(message = "City or both latitude and longitude are required")
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
