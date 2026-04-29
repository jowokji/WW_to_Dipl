package com.weatherwear.controller;

import com.weatherwear.config.SecurityConfig;
import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.exception.ResourceNotFoundException;
import com.weatherwear.exception.UnauthorizedException;
import com.weatherwear.security.JwtAuthFilter;
import com.weatherwear.service.HistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = HistoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryService historyService;

    @Test
    void getHistory_returnsOk() throws Exception {
        when(historyService.getCurrentUserHistory()).thenReturn(List.of(historyResponse()));

        mockMvc.perform(get("/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].city").value("Vilnius"));
    }

    @Test
    void getHistoryDetails_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/history/not-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHistory_unauthorized_returnsUnauthorized() throws Exception {
        when(historyService.getCurrentUserHistory())
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(get("/history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getHistoryDetails_notFound_returnsNotFound() throws Exception {
        when(historyService.getHistoryDetails(99L))
                .thenThrow(new ResourceNotFoundException("Recommendation history not found"));

        mockMvc.perform(get("/history/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void clearHistory_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/history"))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearHistory_unauthorized_returnsUnauthorized() throws Exception {
        doThrow(new UnauthorizedException())
                .when(historyService)
                .clearCurrentUserHistory();

        mockMvc.perform(delete("/history"))
                .andExpect(status().isUnauthorized());
    }

    private HistoryResponse historyResponse() {
        return new HistoryResponse(
                1L,
                "Vilnius",
                "Temp: 12.0, Feels: 10.0, Condition: CLOUDS",
                "Wear a jacket",
                LocalDateTime.of(2026, 4, 29, 12, 0)
        );
    }
}
