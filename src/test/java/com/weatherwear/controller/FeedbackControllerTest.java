package com.weatherwear.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherwear.common.FeedbackType;
import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.feedback.FeedbackRequest;
import com.weatherwear.dto.feedback.FeedbackResponse;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FeedbackController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    @Test
    void createFeedback_returnsOk() throws Exception {
        when(feedbackService.createFeedback(any(FeedbackRequest.class)))
                .thenReturn(feedbackResponse());

        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void createFeedback_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFeedback_returnsOk() throws Exception {
        when(feedbackService.getCurrentUserFeedback())
                .thenReturn(List.of(feedbackResponse()));

        mockMvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feedbackType").value("RATING"));
    }

    @Test
    void deleteFeedback_unauthorized_returnsUnauthorized() throws Exception {
        doThrow(new UnauthorizedException())
                .when(feedbackService)
                .deleteCurrentUserFeedback(1L);

        mockMvc.perform(delete("/feedback/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteFeedback_notFound_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Feedback not found"))
                .when(feedbackService)
                .deleteCurrentUserFeedback(99L);

        mockMvc.perform(delete("/feedback/99"))
                .andExpect(status().isNotFound());
    }

    private FeedbackRequest request() {
        FeedbackRequest request = new FeedbackRequest();
        request.setRecommendationHistoryId(10L);
        request.setFeedbackType(FeedbackType.RATING);
        request.setRating((short) 5);
        return request;
    }

    private FeedbackResponse feedbackResponse() {
        return new FeedbackResponse(
                1L,
                10L,
                FeedbackType.RATING,
                (short) 5,
                "Useful",
                LocalDateTime.of(2026, 4, 29, 12, 0)
        );
    }
}
