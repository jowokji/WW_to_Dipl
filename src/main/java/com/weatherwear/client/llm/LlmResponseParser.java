package com.weatherwear.client.llm;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LlmResponseParser {

    private static final String EMPTY_RESPONSE = "";

    @SuppressWarnings("unchecked")
    public String parseContent(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            return EMPTY_RESPONSE;
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            return EMPTY_RESPONSE;
        }

        Map<String, Object> firstChoice = choices.get(0);

        Map<String, Object> message =
                (Map<String, Object>) firstChoice.get("message");

        if (message == null || !message.containsKey("content")) {
            return EMPTY_RESPONSE;
        }

        Object content = message.get("content");

        if (content == null || content.toString().isBlank()) {
            return EMPTY_RESPONSE;
        }

        return content.toString();
    }
}
