package com.weatherwear.controller;

import com.weatherwear.dto.preference.PreferenceRequest;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public PreferenceResponse getPreferences() {
        return preferenceService.getCurrentUserPreferences();
    }

    @PutMapping
    public PreferenceResponse updatePreferences(
            @Valid @RequestBody PreferenceRequest request
    ) {
        return preferenceService.updateCurrentUserPreferences(request);
    }
}