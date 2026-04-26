package com.weatherwear.exception;

public class LlmApiException extends RuntimeException {

    public LlmApiException(String message) {
        super(message);
    }
}