package com.weatherwear.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherwear.common.ActivityLevel;
import com.weatherwear.common.SensitivityLevel;
import com.weatherwear.common.StylePreference;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.preference.PreferenceRequest;
import com.weatherwear.dto.preference.PreferenceResponse;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.exception.UserNotFoundException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.PreferenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PreferenceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class PreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PreferenceService preferenceService;

    @Test
    void getPreferences_returnsOk() throws Exception {
        when(preferenceService.getCurrentUserPreferences()).thenReturn(preferenceResponse());

        mockMvc.perform(get("/preferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.stylePreference").value("CASUAL"));
    }

    @Test
    void updatePreferences_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPreferences_unauthorized_returnsUnauthorized() throws Exception {
        when(preferenceService.getCurrentUserPreferences())
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(get("/preferences"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPreferences_userNotFound_returnsNotFound() throws Exception {
        when(preferenceService.getCurrentUserPreferences())
                .thenThrow(new UserNotFoundException("missing@example.com"));

        mockMvc.perform(get("/preferences"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePreferences_returnsOk() throws Exception {
        when(preferenceService.updateCurrentUserPreferences(any(PreferenceRequest.class)))
                .thenReturn(preferenceResponse());

        mockMvc.perform(put("/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preferenceRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stylePreference").value("CASUAL"));
    }

    private PreferenceResponse preferenceResponse() {
        return new PreferenceResponse(
                10L,
                StylePreference.CASUAL,
                SensitivityLevel.MEDIUM,
                SensitivityLevel.MEDIUM,
                SensitivityLevel.MEDIUM,
                SensitivityLevel.MEDIUM,
                (short) 3,
                false,
                false,
                ActivityLevel.MEDIUM,
                "black",
                "sandals"
        );
    }

    private PreferenceRequest preferenceRequest() {
        PreferenceRequest request = new PreferenceRequest();
        request.setStylePreference(StylePreference.CASUAL);
        request.setColdSensitivity(SensitivityLevel.MEDIUM);
        request.setHeatSensitivity(SensitivityLevel.MEDIUM);
        return request;
    }
}
