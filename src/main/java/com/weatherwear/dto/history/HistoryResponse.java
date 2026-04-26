package com.weatherwear.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HistoryResponse {

    private Long id;
    private String city;
    private String weatherSummary;
    private String recommendationText;
    private LocalDateTime createdAt;
}