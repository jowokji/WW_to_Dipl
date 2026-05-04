# Getting Started

This guide shows how to run the backend locally and make the first authenticated API call.

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose for PostgreSQL
- Environment values for database, JWT, weather provider, and LLM provider

## Configure Environment

Copy `.env.example` to `.env` and fill in secret values:

```text
POSTGRES_PASSWORD=...
APP_DB_PASSWORD=...
JWT_SECRET=...
WEATHER_API_KEY=...
LLM_API_KEY=...
```

The JWT secret must be long enough for HS256 signing.

## Start Local Infrastructure

```bash
docker compose up -d postgres
```

If PostgreSQL was already initialized with another `APP_DB_PASSWORD`, changing `.env` will not update the existing database user password. Reuse the original password or intentionally recreate the local Docker volume before testing with new credentials.

## Run the Backend

```bash
mvn spring-boot:run
```

Default local base URL:

```text
http://localhost:8090/api
```

Open Swagger UI:

```text
http://localhost:8090/api/swagger-ui.html
```

## First API Call

Register a user:

```bash
curl -X POST "http://localhost:8090/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

Copy the `token` from the response.

Fetch default preferences:

```bash
curl "http://localhost:8090/api/preferences" \
  -H "Authorization: Bearer <token>"
```

Expected result:

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

## Next Steps

Continue with `integration-tutorial.md` for a complete workflow: authenticate, save preferences, generate a recommendation, review history, and submit feedback.
