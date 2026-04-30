# WW_to_Dipl

WeatherWear backend for a diploma project.

## Backend structure

- Java source code: `src/main/java/com/weatherwear`
- Application configuration: `src/main/resources`
- Database schema migrations: `src/main/resources/db/migration`
- Database documentation: `Documentation/database_documentation.md`
- Database logical schema: `Documentation/database_schema.mmd`
- Database roles script: `src/main/resources/db/security/roles_and_grants.sql`
- Database test data: `src/main/resources/db/testdata/weatherwear_test_data.sql`

Flyway migrations are the source of truth for the database schema.
