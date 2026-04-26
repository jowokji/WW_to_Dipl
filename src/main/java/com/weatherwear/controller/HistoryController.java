package com.weatherwear.controller;

import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public List<HistoryResponse> getHistory() {
        return historyService.getCurrentUserHistory();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearHistory() {
        historyService.clearCurrentUserHistory();
        return ResponseEntity.noContent().build();
    }
}