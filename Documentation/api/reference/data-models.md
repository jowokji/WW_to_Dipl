# Data Models

This page documents the request and response DTOs exposed by the WeatherWear REST API.

The canonical machine-readable schema is:

```text
Documentation/api/openapi/weatherwear-api.openapi.json
```

## Authentication Models

### RegisterRequest

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `email` | string | Yes | Valid email |
| `password` | string | Yes | Minimum 6 characters |

### LoginRequest

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `email` | string | Yes | Valid email |
| `password` | string | Yes | Non-blank |

### AuthResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `token` | string | JWT access token |
| `email` | string | Authenticated user's email |
| `role` | enum | `USER` or `ADMIN` |

## WeatherResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `city` | string | City name from weather provider |
| `latitude` | number | Latitude |
| `longitude` | number | Longitude |
| `temperature` | number | Celsius temperature |
| `feelsLike` | number | Celsius feels-like temperature |
| `humidity` | integer | Relative humidity percent |
| `windSpeed` | number | Wind speed in meters per second |
| `condition` | enum | Normalized weather condition |
| `precipitation` | number | Precipitation value when supplied by provider |
| `cached` | boolean | Whether data came from local cache |

## Recommendation Models

### RecommendationRequest

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `city` | string | Conditional | Required unless coordinates are supplied, max 120 |
| `latitude` | number | Conditional | Required with longitude if city is absent, `-90..90` |
| `longitude` | number | Conditional | Required with latitude if city is absent, `-180..180` |
| `occasion` | string | No | Free text, for example `work`, `walk`, `date`, `sport` |

### RecommendationResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `city` | string | Weather city used for the recommendation |
| `weatherSummary` | string | Compact weather summary used in history |
| `recommendation` | string | AI-generated clothing advice |

## Preference Models

### PreferenceRequest

| Field | Type | Required | Validation or default |
| --- | --- | --- | --- |
| `stylePreference` | enum | Yes | See enum table |
| `coldSensitivity` | enum | Yes | See enum table |
| `heatSensitivity` | enum | Yes | See enum table |
| `windSensitivity` | enum | No | Defaults to `MEDIUM` |
| `rainSensitivity` | enum | No | Defaults to `MEDIUM` |
| `maxLayers` | integer | No | `1..5`, defaults to `3` |
| `prefersHeadwear` | boolean | No | Defaults to `false` |
| `prefersWaterproof` | boolean | No | Defaults to `false` |
| `activityLevel` | enum | No | Defaults to `MEDIUM` |
| `preferredColors` | string | No | Free text |
| `avoidItems` | string | No | Free text |

### PreferenceResponse

Same fields as `PreferenceRequest` plus:

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | integer | Preference row identifier |

## HistoryResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | integer | Recommendation history identifier |
| `city` | string | City used for the recommendation |
| `weatherSummary` | string | Saved weather summary |
| `recommendationText` | string | Saved AI recommendation |
| `createdAt` | date-time | Creation timestamp |

## Feedback Models

### FeedbackRequest

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `recommendationHistoryId` | integer | Yes | Must belong to current user |
| `feedbackType` | enum | No | Defaults to `RATING` |
| `rating` | integer | Conditional | `1..5`; required if comment is absent |
| `comment` | string | Conditional | Required if rating is absent |

### FeedbackResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | integer | Feedback identifier |
| `recommendationHistoryId` | integer | Related recommendation history item |
| `feedbackType` | enum | Feedback category |
| `rating` | integer | Optional numeric rating |
| `comment` | string | Optional comment |
| `createdAt` | date-time | Creation timestamp |

## Chat Models

### ChatRequest

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `sessionId` | integer | No | Existing session owned by current user |
| `message` | string | Yes | Non-blank, max 3000 characters |
| `city` | string | No | Max 120 characters; used for weather context |

### ChatResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `sessionId` | integer | Chat session identifier |
| `answer` | string | AI assistant response |
| `createdAt` | date-time | Assistant message timestamp |

### ChatSessionResponse

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | integer | Chat session identifier |
| `title` | string | Session title, currently `Style chat` |
| `createdAt` | date-time | Session creation timestamp |
| `updatedAt` | date-time | Last assistant response timestamp |

### ChatMessageDto

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | integer | Message identifier |
| `role` | enum | `USER`, `ASSISTANT`, or `SYSTEM` |
| `content` | string | Message text |
| `createdAt` | date-time | Message timestamp |

## Enums

| Enum | Values |
| --- | --- |
| `Role` | `USER`, `ADMIN` |
| `WeatherCondition` | `CLEAR`, `CLOUDS`, `RAIN`, `DRIZZLE`, `THUNDERSTORM`, `SNOW`, `MIST`, `FOG`, `WIND`, `UNKNOWN` |
| `StylePreference` | `CASUAL`, `BUSINESS`, `SPORTY`, `STREETWEAR`, `ELEGANT`, `MINIMALIST` |
| `SensitivityLevel` | `LOW`, `MEDIUM`, `HIGH` |
| `ActivityLevel` | `LOW`, `MEDIUM`, `HIGH` |
| `FeedbackType` | `RATING`, `LIKE`, `DISLIKE`, `COMMENT` |
| `ChatRole` | `USER`, `ASSISTANT`, `SYSTEM` |

