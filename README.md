# WW_to_Dipl

WeatherWear backend for a diploma project.

## Prerequisites

Before running the project locally, install:

- Java 17 or newer
- Maven
- Docker Desktop
- Android Studio with Android SDK, if you want to run the Android client

## Backend structure

- Java source code: `src/main/java/com/weatherwear`
- Application configuration: `src/main/resources`
- Minimal Android client: `android`
- Database schema migrations: `src/main/resources/db/migration`
- Database documentation: `Documentation/database_documentation.md`
- Database logical schema: `Documentation/database_schema.mmd`
- API documentation: `Documentation/api/README.md`
- API OpenAPI specification: `Documentation/api/openapi/weatherwear-api.openapi.json`
- AI safety and prompt documentation: `Documentation/ai_safety.md`
- Cloud deployment evidence checklist: `Documentation/deployment_evidence.md`
- Database roles script: `src/main/resources/db/security/roles_and_grants.sql`
- Database test data: `src/main/resources/db/testdata/weatherwear_test_data.sql`

Flyway migrations are the source of truth for the database schema.

## Local Docker startup

Create a local environment file before running Docker Compose:

```bash
cp .env.example .env
```

Then open `.env` and replace the placeholder values for:

- `POSTGRES_PASSWORD`
- `APP_DB_PASSWORD`
- `JWT_SECRET`
- `WEATHER_API_KEY`
- `LLM_API_KEY`

Docker Compose reads `.env` automatically from the project root. The file is ignored by Git because it contains local secrets.

After startup, the local backend is available at:

- API base URL: `http://localhost:8090/api`
- Health check: `http://localhost:8090/api/health`
- Swagger UI: `http://localhost:8090/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8090/api/api-docs`

To stop the local services, run:

```bash
docker compose down
```

To stop the services and remove local database volumes, run:

```bash
docker compose down -v
```

## Verification Commands

Run these before final submission:

```bash
mvn clean test
mvn package
docker compose up --build
```

The Maven `verify` lifecycle also runs JaCoCo coverage enforcement. The configured threshold is 70% instruction coverage after excluding configuration, DTO, and entity packages.

## Android Demo Quickstart

1. Start the backend from the project root:

   ```bash
   docker compose up --build
   ```

2. Open the `android` folder in Android Studio and run the `app` configuration.
3. Set the backend URL on the Android Profile screen:

   - Emulator: `http://10.0.2.2:8090/api`
   - Physical phone: `http://<host-lan-ip>:8090/api`

4. Tap `Health` in the app to verify connectivity.

More Android setup, APK build, and troubleshooting notes are in `android/README.md`.
