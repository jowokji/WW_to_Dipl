package com.weatherwear.client.llm;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LlmResponseParser {

    @SuppressWarnings("unchecked")
    public String parseContent(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            return "No response from AI assistant.";
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            return "No response from AI assistant.";
        }

        Map<String, Object> firstChoice = choices.get(0);

        Map<String, Object> message =
                (Map<String, Object>) firstChoice.get("message");

        if (message == null || !message.containsKey("content")) {
            return "No response from AI assistant.";
        }

        Object content = message.get("content");

        return content != null ? content.toString() : "No response from AI assistant.";
    }
}