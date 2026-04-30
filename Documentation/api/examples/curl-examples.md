# API Examples

These examples assume the backend is running at:

```text
http://localhost:8090/api
```

For protected endpoints, replace `<token>` with a JWT returned by registration or login.

## Register

```bash
curl -X POST "http://localhost:8090/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

## Login

```bash
curl -X POST "http://localhost:8090/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

## Get Weather by City

```bash
curl "http://localhost:8090/api/weather?city=Vilnius" \
  -H "Authorization: Bearer <token>"
```

## Get Weather by Coordinates

```bash
curl "http://localhost:8090/api/weather/coordinates?lat=54.6872&lon=25.2797" \
  -H "Authorization: Bearer <token>"
```

## Create Preferences

```bash
curl -X POST "http://localhost:8090/api/preferences" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"stylePreference\":\"CASUAL\",\"coldSensitivity\":\"MEDIUM\",\"heatSensitivity\":\"LOW\",\"maxLayers\":3,\"prefersHeadwear\":true,\"prefersWaterproof\":true,\"activityLevel\":\"MEDIUM\"}"
```

## Generate Recommendation

```bash
curl -X POST "http://localhost:8090/api/recommendations" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"city\":\"Vilnius\",\"occasion\":\"work\"}"
```

## List History

```bash
curl "http://localhost:8090/api/history" \
  -H "Authorization: Bearer <token>"
```

## Get One History Item

```bash
curl "http://localhost:8090/api/history/101" \
  -H "Authorization: Bearer <token>"
```

## Create Feedback

```bash
curl -X POST "http://localhost:8090/api/feedback" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"recommendationHistoryId\":101,\"feedbackType\":\"RATING\",\"rating\":5,\"comment\":\"Useful recommendation for windy weather.\"}"
```

## Start Chat Session

```bash
curl -X POST "http://localhost:8090/api/chat" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"What should I wear for a long walk today?\",\"city\":\"Vilnius\"}"
```

## Continue Chat Session

```bash
curl -X POST "http://localhost:8090/api/chat" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\":12,\"message\":\"Can you make it more business casual?\"}"
```

## Health Check

```bash
curl "http://localhost:8090/api/health"
```

Expected response:

```json
{
  "status": "UP",
  "service": "WeatherWear Backend",
  "timestamp": "2026-04-30T12:45:00"
}
```

