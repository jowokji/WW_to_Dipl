package com.weatherwear.config;

public final class OpenApiExamples {

    public static final String REGISTER_REQUEST = """
            {
              "email": "alex@example.com",
              "password": "securePass123"
            }
            """;

    public static final String LOGIN_REQUEST = REGISTER_REQUEST;

    public static final String AUTH_RESPONSE = """
            {
              "token": "eyJhbGciOiJIUzI1NiJ9.example.signature",
              "email": "alex@example.com",
              "role": "USER"
            }
            """;

    public static final String WEATHER_RESPONSE = """
            {
              "city": "Vilnius",
              "latitude": 54.6872,
              "longitude": 25.2797,
              "temperature": 12.4,
              "feelsLike": 10.8,
              "humidity": 71,
              "windSpeed": 4.6,
              "condition": "CLOUDS",
              "precipitation": 0.0,
              "cached": false
            }
            """;

    public static final String CACHED_WEATHER_RESPONSE = """
            {
              "city": "Vilnius",
              "latitude": 54.6872,
              "longitude": 25.2797,
              "temperature": 12.1,
              "feelsLike": 10.5,
              "humidity": 72,
              "windSpeed": 4.2,
              "condition": "CLOUDS",
              "precipitation": 0.0,
              "cached": true
            }
            """;

    public static final String RECOMMENDATION_REQUEST_CITY = """
            {
              "city": "Vilnius",
              "occasion": "work"
            }
            """;

    public static final String RECOMMENDATION_REQUEST_COORDINATES = """
            {
              "latitude": 54.6872,
              "longitude": 25.2797,
              "occasion": "walk"
            }
            """;

    public static final String RECOMMENDATION_RESPONSE = """
            {
              "city": "Vilnius",
              "weatherSummary": "Temp: 12.4, Feels: 10.8, Condition: CLOUDS",
              "recommendation": "Wear a light waterproof jacket, a long-sleeve shirt, jeans, and closed shoes. Add a scarf if you are sensitive to wind."
            }
            """;

    public static final String PREFERENCE_REQUEST = """
            {
              "stylePreference": "CASUAL",
              "coldSensitivity": "MEDIUM",
              "heatSensitivity": "LOW",
              "windSensitivity": "HIGH",
              "rainSensitivity": "MEDIUM",
              "maxLayers": 3,
              "prefersHeadwear": true,
              "prefersWaterproof": true,
              "activityLevel": "MEDIUM",
              "preferredColors": "black, navy, grey",
              "avoidItems": "wool sweaters"
            }
            """;

    public static final String PREFERENCE_RESPONSE = """
            {
              "id": 7,
              "stylePreference": "CASUAL",
              "coldSensitivity": "MEDIUM",
              "heatSensitivity": "LOW",
              "windSensitivity": "HIGH",
              "rainSensitivity": "MEDIUM",
              "maxLayers": 3,
              "prefersHeadwear": true,
              "prefersWaterproof": true,
              "activityLevel": "MEDIUM",
              "preferredColors": "black, navy, grey",
              "avoidItems": "wool sweaters"
            }
            """;

    public static final String HISTORY_RESPONSE = """
            {
              "id": 101,
              "city": "Vilnius",
              "weatherSummary": "Temp: 12.4, Feels: 10.8, Condition: CLOUDS",
              "recommendationText": "Wear a light waterproof jacket and closed shoes.",
              "createdAt": "2026-04-30T12:45:00"
            }
            """;

    public static final String HISTORY_LIST_RESPONSE = """
            [
              {
                "id": 101,
                "city": "Vilnius",
                "weatherSummary": "Temp: 12.4, Feels: 10.8, Condition: CLOUDS",
                "recommendationText": "Wear a light waterproof jacket and closed shoes.",
                "createdAt": "2026-04-30T12:45:00"
              }
            ]
            """;

    public static final String FEEDBACK_REQUEST = """
            {
              "recommendationHistoryId": 101,
              "feedbackType": "RATING",
              "rating": 5,
              "comment": "Useful recommendation for windy weather."
            }
            """;

    public static final String FEEDBACK_RESPONSE = """
            {
              "id": 501,
              "recommendationHistoryId": 101,
              "feedbackType": "RATING",
              "rating": 5,
              "comment": "Useful recommendation for windy weather.",
              "createdAt": "2026-04-30T12:45:00"
            }
            """;

    public static final String FEEDBACK_LIST_RESPONSE = """
            [
              {
                "id": 501,
                "recommendationHistoryId": 101,
                "feedbackType": "RATING",
                "rating": 5,
                "comment": "Useful recommendation for windy weather.",
                "createdAt": "2026-04-30T12:45:00"
              }
            ]
            """;

    public static final String CHAT_REQUEST_NEW_SESSION = """
            {
              "message": "What should I wear for a long walk today?",
              "city": "Vilnius"
            }
            """;

    public static final String CHAT_REQUEST_EXISTING_SESSION = """
            {
              "sessionId": 12,
              "message": "Should I add a rain jacket?",
              "city": "Vilnius"
            }
            """;

    public static final String CHAT_RESPONSE = """
            {
              "sessionId": 12,
              "answer": "Choose a breathable base layer, a light jacket, comfortable trousers, and shoes with grip.",
              "createdAt": "2026-04-30T12:45:00"
            }
            """;

    public static final String CHAT_SESSIONS_RESPONSE = """
            [
              {
                "id": 12,
                "title": "Style chat",
                "createdAt": "2026-04-30T12:40:00",
                "updatedAt": "2026-04-30T12:45:00"
              }
            ]
            """;

    public static final String CHAT_MESSAGES_RESPONSE = """
            [
              {
                "id": 9001,
                "role": "USER",
                "content": "What should I wear for a long walk today?",
                "createdAt": "2026-04-30T12:44:00"
              },
              {
                "id": 9002,
                "role": "ASSISTANT",
                "content": "Choose a breathable base layer, a light jacket, comfortable trousers, and shoes with grip.",
                "createdAt": "2026-04-30T12:45:00"
              }
            ]
            """;

    public static final String HEALTH_RESPONSE = """
            {
              "status": "UP",
              "service": "WeatherWear Backend",
              "timestamp": "2026-04-30T12:45:00"
            }
            """;

    public static final String VALIDATION_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 400,
              "error": "Bad Request",
              "message": "email: Email must be valid",
              "path": "/api/auth/register"
            }
            """;

    public static final String UNAUTHORIZED_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 401,
              "error": "Unauthorized",
              "message": "Authentication is required",
              "path": "/api/preferences"
            }
            """;

    public static final String FORBIDDEN_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 403,
              "error": "Forbidden",
              "message": "Access denied",
              "path": "/api/admin"
            }
            """;

    public static final String NOT_FOUND_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 404,
              "error": "Not Found",
              "message": "Recommendation history not found",
              "path": "/api/history/999"
            }
            """;

    public static final String CONFLICT_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 409,
              "error": "Conflict",
              "message": "User with email alex@example.com already exists",
              "path": "/api/auth/register"
            }
            """;

    public static final String BAD_GATEWAY_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 502,
              "error": "Bad Gateway",
              "message": "Failed to fetch weather data",
              "path": "/api/weather"
            }
            """;

    public static final String SERVER_ERROR = """
            {
              "timestamp": "2026-04-30T12:45:00",
              "status": 500,
              "error": "Internal Server Error",
              "message": "Unexpected server error",
              "path": "/api/recommendations"
            }
            """;

    private OpenApiExamples() {
    }
}
