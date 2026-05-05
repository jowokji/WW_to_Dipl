package com.weatherwear.mobile;

import android.os.Handler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class ApiClient {

    private static final int TIMEOUT_MS = 30000;
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8090/api";

    private final Handler handler;

    ApiClient(Handler handler) {
        this.handler = handler;
    }

    void request(
            String method,
            String baseUrl,
            String path,
            String token,
            JSONObject body,
            ApiCallback callback
    ) {
        new Thread(() -> execute(method, baseUrl, path, token, body, callback)).start();
    }

    private void execute(
            String method,
            String baseUrl,
            String path,
            String token,
            JSONObject body,
            ApiCallback callback
    ) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(normalizeBaseUrl(baseUrl) + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");

            if (token != null && !token.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(payload);
                }
            }

            int status = connection.getResponseCode();
            String responseBody = readBody(status >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream());

            if (status >= 200 && status < 300) {
                postSuccess(callback, responseBody);
            } else {
                postError(callback, formatError(status, responseBody));
            }
        } catch (Exception ex) {
            postError(callback, formatNetworkError(ex));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String normalizeBaseUrl(String baseUrl) {
        String value = baseUrl == null || baseUrl.trim().isEmpty()
                ? DEFAULT_BASE_URL
                : baseUrl.trim();

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String readBody(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                stream,
                StandardCharsets.UTF_8
        ))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        return body.toString();
    }

    private void postSuccess(ApiCallback callback, String body) {
        handler.post(() -> callback.onSuccess(body));
    }

    private void postError(ApiCallback callback, String message) {
        handler.post(() -> callback.onError(message));
    }

    private String formatError(int status, String responseBody) {
        if (status == 401) {
            return "HTTP 401: Login expired or token is missing.";
        }

        if (status == 403) {
            return "HTTP 403: This action requires a valid login.";
        }

        if (status >= 500) {
            return "HTTP " + status + ": Backend error. Check Docker logs.";
        }

        if (responseBody == null || responseBody.trim().isEmpty()) {
            return "HTTP " + status;
        }

        try {
            JSONObject json = new JSONObject(responseBody);
            String message = json.optString("message");
            if (!message.trim().isEmpty()) {
                return "HTTP " + status + ": " + message;
            }
        } catch (Exception ignored) {
        }

        return "HTTP " + status + ": " + responseBody;
    }

    private String formatNetworkError(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Network error.";
        }

        String lower = message.toLowerCase();
        if (lower.contains("failed to connect") || lower.contains("connection refused")) {
            return "Backend is unavailable. Start Docker and check the Backend URL.";
        }

        if (lower.contains("timeout")) {
            return "Request timed out. Check backend, network, or API provider keys.";
        }

        return message;
    }
}
