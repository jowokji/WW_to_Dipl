package com.weatherwear.client.llm;

import com.weatherwear.exception.LlmApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LlmClient {

    private final RestClient restClient;
    private final LlmResponseParser responseParser;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.api.key}")
    private String apiKey;

    public String generateRecommendation(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", new Object[]{
                            Map.of("role", "user", "content", prompt)
                    }
            );

            Map<String, Object> response = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            return responseParser.parseContent(response);

        } catch (Exception ex) {
            throw new LlmApiException("Failed to get response from AI assistant");
        }
    }
}
