# WeatherWear Cloud Deployment Evidence

Reviewed: 2026-05-04

## Production URLs

Configured production base URL:

```text
https://wwtodipl-production.up.railway.app/api
```

Swagger UI:

```text
https://wwtodipl-production.up.railway.app/api/swagger-ui.html
```

Generated OpenAPI JSON:

```text
https://wwtodipl-production.up.railway.app/api/api-docs
```

Health check:

```text
https://wwtodipl-production.up.railway.app/api/health
```

## Live Endpoint Verification

Verified from the development workstation on 2026-05-04:

| Endpoint | Result |
| --- | --- |
| `https://wwtodipl-production.up.railway.app/api/health` | `200 OK`, returned `status=UP` |
| `https://wwtodipl-production.up.railway.app/api/swagger-ui.html` | `200 OK` |
| `https://wwtodipl-production.up.railway.app/api/api-docs` | `200 OK` |

## Required Railway Variables

| Variable | Purpose |
| --- | --- |
| `SPRING_PROFILES_ACTIVE=prod` | Enables production datasource, Flyway, and OpenAPI server URL behavior |
| `PGHOST` | Railway PostgreSQL host |
| `PGPORT` | Railway PostgreSQL port |
| `PGDATABASE` | Railway PostgreSQL database name |
| `PGUSER` | Application database user |
| `PGPASSWORD` | Application database password |
| `JWT_SECRET` | HS256 JWT signing secret |
| `JWT_EXPIRATION` | JWT lifetime in milliseconds |
| `WEATHER_API_KEY` | OpenWeather API key |
| `WEATHER_API_URL` | Weather provider endpoint, defaults to OpenWeather current weather |
| `LLM_API_KEY` | LLM provider API key |
| `LLM_API_URL` | LLM chat completions endpoint |
| `OPENAPI_SERVER_URL` | Public API base URL shown in Swagger/OpenAPI |

## Evidence to Attach to Final Submission

1. Railway deployment log showing successful build and start.
2. GitHub Actions run showing code checks, build, tests, package, and deployment trigger.
3. Screenshot of Railway variables with secret values hidden.
4. Browser screenshot of Swagger UI at the production URL.
5. Health check output:

   ```bash
   curl -i https://wwtodipl-production.up.railway.app/api/health
   ```

6. OpenAPI availability check:

   ```bash
   curl -i https://wwtodipl-production.up.railway.app/api/api-docs
   ```

## Current Limitation

This repository documents and verifies the public endpoints, but the final submission should still include external screenshots or logs from the owner's Railway and GitHub accounts.
