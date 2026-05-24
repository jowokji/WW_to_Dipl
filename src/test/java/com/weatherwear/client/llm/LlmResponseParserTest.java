package com.weatherwear.client.llm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LlmResponseParserTest {

    private final LlmResponseParser parser = new LlmResponseParser();

    @Test
    void parseContent_success() {
        Map<String, Object> response = Map.of(
                "choices",
                List.of(Map.of("message", Map.of("content", "Wear a jacket")))
        );

        assertThat(parser.parseContent(response)).isEqualTo("Wear a jacket");
    }

    @Test
    void parseContent_nullResponse_returnsEmpty() {
        assertThat(parser.parseContent(null)).isEmpty();
    }

    @Test
    void parseContent_missingChoices_returnsEmpty() {
        assertThat(parser.parseContent(Map.of())).isEmpty();
    }

    @Test
    void parseContent_emptyChoices_returnsEmpty() {
        assertThat(parser.parseContent(Map.of("choices", List.of()))).isEmpty();
    }

    @Test
    void parseContent_missingMessage_returnsEmpty() {
        assertThat(parser.parseContent(Map.of("choices", List.of(Map.of())))).isEmpty();
    }

    @Test
    void parseContent_nullContent_returnsEmpty() {
        Map<String, Object> response = Map.of(
                "choices",
                List.of(Map.of("message", new java.util.HashMap<String, Object>() {{
                    put("content", null);
                }}))
        );

        assertThat(parser.parseContent(response)).isEmpty();
    }

    @Test
    void parseContent_blankContent_returnsEmpty() {
        Map<String, Object> response = Map.of(
                "choices",
                List.of(Map.of("message", Map.of("content", "   ")))
        );

        assertThat(parser.parseContent(response)).isEmpty();
    }
}
