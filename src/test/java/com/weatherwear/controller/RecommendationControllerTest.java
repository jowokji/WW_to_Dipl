package com.weatherwear.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.recommendation.RecommendationRequest;
import com.weatherwear.dto.recommendation.RecommendationResponse;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.RecommendationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RecommendationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void getRecommendation_returnsOk() throws Exception {
        when(recommendationService.getRecommendation(any(RecommendationRequest.class)))
                .thenReturn(new RecommendationResponse(
                        "Vilnius",
                        "Temp: 12.0, Feels: 10.0, Condition: CLOUDS",
                        "Wear a jacket"
                ));

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Vilnius"))
                .andExpect(jsonPath("$.recommendation").value("Wear a jacket"));
    }

    @Test
    void getRecommendation_missingLocation_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRecommendation_unauthorized_returnsUnauthorized() throws Exception {
        when(recommendationService.getRecommendation(any(RecommendationRequest.class)))
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRecommendation_notFound_returnsNotFound() throws Exception {
        when(recommendationService.getRecommendation(any(RecommendationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Resource not found"));

        mockMvc.perform(post("/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isNotFound());
    }

    private RecommendationRequest request() {
        RecommendationRequest request = new RecommendationRequest();
        request.setCity("Vilnius");
        request.setOccasion("work");
        return request;
    }
}
