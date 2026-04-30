# Integration Tutorial

This tutorial demonstrates a practical end-to-end integration workflow for a client application.

Scenario: a mobile app user signs in, configures style preferences, requests a clothing recommendation for Vilnius, reviews history, and submits feedback.

## 1. Register or Log In

```bash
curl -X POST "http://localhost:8090/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

Save the returned token:

```text
TOKEN=eyJhbGciOiJIUzI1NiJ9.example.signature
```

## 2. Save User Preferences

```bash
curl -X POST "http://localhost:8090/api/preferences" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"stylePreference\": \"CASUAL\",
    \"coldSensitivity\": \"MEDIUM\",
    \"heatSensitivity\": \"LOW\",
    \"windSensitivity\": \"HIGH\",
    \"rainSensitivity\": \"MEDIUM\",
    \"maxLayers\": 3,
    \"prefersHeadwear\": true,
    \"prefersWaterproof\": true,
    \"activityLevel\": \"MEDIUM\",
    \"preferredColors\": \"black, navy, grey\",
    \"avoidItems\": \"wool sweaters\"
  }"
```

Why this matters: recommendation prompts use saved preferences to personalize the AI response.

## 3. Check Weather

```bash
curl "http://localhost:8090/api/weather?city=Vilnius" \
  -H "Authorization: Bearer $TOKEN"
```

The response includes `cached`. If `cached` is `true`, the value came from the 30-minute local cache.

## 4. Generate a Recommendation

```bash
curl -X POST "http://localhost:8090/api/recommendations" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"city\":\"Vilnius\",\"occasion\":\"work\"}"
```

Successful response:

```json
{
  "city": "Vilnius",
  "weatherSummary": "Temp: 12.4, Feels: 10.8, Condition: CLOUDS",
  "recommendation": "Wear a light waterproof jacket, a long-sleeve shirt, jeans, and closed shoes."
}
```

The backend automatically stores the generated recommendation in the current user's history.

## 5. Read Recommendation History

```bash
curl "http://localhost:8090/api/history" \
  -H "Authorization: Bearer $TOKEN"
```

Copy an `id` from the response. This is the `recommendationHistoryId` needed for feedback.

## 6. Submit Feedback

```bash
curl -X POST "http://localhost:8090/api/feedback" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"recommendationHistoryId\": 101,
    \"feedbackType\": \"RATING\",
    \"rating\": 5,
    \"comment\": \"Useful recommendation for windy weather.\"
  }"
```

## 7. Ask a Follow-up Chat Question

```bash
curl -X POST "http://localhost:8090/api/chat" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"Can you make the outfit more business casual?\",\"city\":\"Vilnius\"}"
```

Save `sessionId` to continue the same conversation later.

## Edge Cases to Handle in Client Code

| Scenario | Expected status | Client behavior |
| --- | --- | --- |
| Token expired | `401` | Redirect to login |
| City missing in recommendation request | `400` | Ask user to select city or enable location |
| Weather provider unavailable | `502` | Show retry message |
| History ID belongs to another user | `404` | Treat as unavailable resource |
| Chat message longer than 3000 characters | `400` | Ask user to shorten message |

