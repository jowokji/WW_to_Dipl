package com.weatherwear.controller;

import com.weatherwear.dto.history.HistoryResponse;
import com.weatherwear.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}")
    public HistoryResponse getHistoryDetails(@PathVariable Long id) {
        return historyService.getHistoryDetails(id);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearHistory() {
        historyService.clearCurrentUserHistory();
        return ResponseEntity.noContent().build();
    }
}
