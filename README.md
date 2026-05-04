# WW_to_Dipl

WeatherWear backend for a diploma project.

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

## Verification Commands

Run these before final submission:

```bash
mvn clean test
mvn package
docker compose up --build
```

The Maven `verify` lifecycle also runs JaCoCo coverage enforcement. The configured threshold is 70% instruction coverage after excluding configuration, DTO, and entity packages.
