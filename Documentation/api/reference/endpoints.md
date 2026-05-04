# API Endpoint Reference

Base URL:

```text
http://localhost:8090/api
```

Authentication format for protected endpoints:

```http
Authorization: Bearer <jwt-token>
```

## Endpoint Summary

| Method | Path | Purpose | Auth | Main success code |
| --- | --- | --- | --- | --- |
| POST | `/auth/register` | Create user account and issue JWT | No | 201 |
| POST | `/auth/login` | Authenticate user and issue JWT | No | 200 |
| DELETE | `/users/me` | Delete current account and associated user-owned data | Yes | 204 |
| GET | `/weather?city={city}` | Get current weather by city | Yes | 200 |
| GET | `/weather/coordinates?lat={lat}&lon={lon}` | Get current weather by coordinates | Yes | 200 |
| POST | `/recommendations` | Generate and save clothing recommendation | Yes | 200 |
| GET | `/preferences` | Get preferences, creating defaults if absent | Yes | 200 |
| POST | `/preferences` | Create or overwrite preferences | Yes | 200 |
| PUT | `/preferences` | Update preferences | Yes | 200 |
| GET | `/history` | List current user's recommendation history | Yes | 200 |
| GET | `/history/{id}` | Get one history item | Yes | 200 |
| DELETE | `/history` | Clear current user's history | Yes | 204 |
| POST | `/feedback` | Add feedback to a recommendation | Yes | 200 |
| GET | `/feedback` | List current user's feedback | Yes | 200 |
| GET | `/feedback/recommendations/{recommendationHistoryId}` | List feedback for one recommendation | Yes | 200 |
| DELETE | `/feedback/{feedbackId}` | Delete feedback | Yes | 204 |
| POST | `/chat` | Send message to AI style assistant | Yes | 200 |
| GET | `/chat/sessions` | List chat sessions | Yes | 200 |
| GET | `/chat/sessions/{id}` | List messages in a session | Yes | 200 |
| DELETE | `/chat/sessions/{id}` | Delete chat session | Yes | 204 |
| GET | `/health` | Check service status | No | 200 |

## Authentication

### POST `/auth/register`

Creates a new user with role `USER`, stores a BCrypt password hash, and returns a JWT.

Request:

```json
{
  "email": "alex@example.com",
  "password": "securePass123"
}
```

Success response `201 Created`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.example.signature",
  "email": "alex@example.com",
  "role": "USER"
}
```

Common errors: `400` invalid email or password shorter than 6 characters, `409` duplicate email.

### POST `/auth/login`

Authenticates an existing user and returns a JWT.

Request:

```json
{
  "email": "alex@example.com",
  "password": "securePass123"
}
```

Success response `200 OK`: same structure as registration.

Common errors: `400` invalid request body, `401` invalid email or password.

## Account

### DELETE `/users/me`

Deletes the authenticated user account and returns `204 No Content`.

The database schema uses `ON DELETE CASCADE` for user-owned preferences, recommendation history, feedback, chat sessions, and chat messages. After deletion, the previously issued JWT can no longer resolve to a user.

Common errors: `401` missing or invalid JWT.

## Weather

### GET `/weather?city=Vilnius`

Returns current weather for a city. The backend caches weather records for 30 minutes.

Success response `200 OK`:

```json
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
```

Common errors: `400` missing or invalid city, `401` missing JWT, `502` OpenWeather dependency failure.

### GET `/weather/coordinates?lat=54.6872&lon=25.2797`

Returns weather for valid coordinates. Latitude range is `-90..90`; longitude range is `-180..180`.

Common errors: `400` invalid coordinate range, `401` missing JWT, `502` OpenWeather dependency failure.

## Recommendations

### POST `/recommendations`

Generates an AI clothing recommendation and stores it in recommendation history.

Request by city:

```json
{
  "city": "Vilnius",
  "occasion": "work"
}
```

Request by coordinates:

```json
{
  "latitude": 54.6872,
  "longitude": 25.2797,
  "occasion": "walk"
}
```

Success response `200 OK`:

```json
{
  "city": "Vilnius",
  "weatherSummary": "Temp: 12.4, Feels: 10.8, Condition: CLOUDS",
  "recommendation": "Wear a light waterproof jacket, a long-sleeve shirt, jeans, and closed shoes."
}
```

Validation rule: provide either `city` or both `latitude` and `longitude`.

Common errors: `400` missing location, `401` missing JWT, `502` weather or LLM dependency failure.

## Preferences

### GET `/preferences`

Returns current user's preferences. If no preference row exists, the service creates defaults:

```json
{
  "id": 1,
  "stylePreference": "CASUAL",
  "coldSensitivity": "MEDIUM",
  "heatSensitivity": "MEDIUM",
  "windSensitivity": "MEDIUM",
  "rainSensitivity": "MEDIUM",
  "maxLayers": 3,
  "prefersHeadwear": false,
  "prefersWaterproof": false,
  "activityLevel": "MEDIUM",
  "preferredColors": "",
  "avoidItems": ""
}
```

### POST `/preferences` and PUT `/preferences`

Both endpoints accept the same request body. Required fields are `stylePreference`, `coldSensitivity`, and `heatSensitivity`.

```json
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
```

## History

### GET `/history`

Returns recommendation history for the current user, ordered newest first.

### GET `/history/{id}`

Returns one history item if it belongs to the current user. Otherwise, the API returns `404`.

### DELETE `/history`

Deletes all recommendation history owned by the current user and returns `204 No Content`.

## Feedback

### POST `/feedback`

Creates feedback for a recommendation owned by the current user. The request must include `recommendationHistoryId` and at least one of `rating` or non-blank `comment`.

```json
{
  "recommendationHistoryId": 101,
  "feedbackType": "RATING",
  "rating": 5,
  "comment": "Useful recommendation for windy weather."
}
```

### GET `/feedback`

Lists all feedback from the current user.

### GET `/feedback/recommendations/{recommendationHistoryId}`

Lists feedback for one recommendation if it belongs to the current user.

### DELETE `/feedback/{feedbackId}`

Deletes feedback owned by the current user and returns `204 No Content`.

## Chat

### POST `/chat`

Sends a message to the AI style assistant. If `sessionId` is omitted, the backend creates a new session named `Style chat`. If `city` is supplied, the prompt includes current weather for that city.

```json
{
  "message": "What should I wear for a long walk today?",
  "city": "Vilnius"
}
```

Success response:

```json
{
  "sessionId": 12,
  "answer": "Choose a breathable base layer, a light jacket, comfortable trousers, and shoes with grip.",
  "createdAt": "2026-04-30T12:45:00"
}
```

### GET `/chat/sessions`

Lists chat sessions ordered by most recent update.

### GET `/chat/sessions/{id}`

Lists session messages in chronological order.

### DELETE `/chat/sessions/{id}`

Deletes one chat session owned by the current user and returns `204 No Content`.

## Health

### GET `/health`

Public service status endpoint.

```json
{
  "status": "UP",
  "service": "WeatherWear Backend",
  "timestamp": "2026-04-30T12:45:00"
}
```
