package com.weatherwear.controller;

import com.weatherwear.dto.preference.PreferenceRequest;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public PreferenceResponse getPreferences() {
        return preferenceService.getCurrentUserPreferences();
    }

    @PostMapping
    public PreferenceResponse createPreferences(
            @Valid @RequestBody PreferenceRequest request
    ) {
        return preferenceService.createCurrentUserPreferences(request);
    }

    @PutMapping
    public PreferenceResponse updatePreferences(
            @Valid @RequestBody PreferenceRequest request
    ) {
        return preferenceService.updateCurrentUserPreferences(request);
    }
}
