# API Documentation Changelog

## 1.0.1 - 2026-05-04

Improved runtime Swagger UI and static OpenAPI example coverage.

Added:

- Swagger tags, operation summaries, descriptions, status codes, and response examples on controllers.
- JWT security annotations only on protected endpoint groups.
- Schema descriptions and examples on DTOs and enums.
- Shared Java OpenAPI example constants for consistent request, response, and error examples.
- Missing success-response examples in `openapi/weatherwear-api.openapi.json`.
- Embedded component and request-response lifecycle diagrams in the architecture overview.
- PNG exports for all API architecture diagrams under `architecture/png/`.
- Local Pillow renderer script for regenerating diagram PNG files.
- Validation notes for JSON syntax, internal `$ref` resolution, example coverage, and Maven tests.

## 1.0.0 - 2026-04-30

Initial version-controlled API documentation set for WeatherWear.

Added:

- OpenAPI 3.0.3 JSON specification.
- Endpoint reference for authentication, weather, recommendations, preferences, history, feedback, chat, and health.
- Data model documentation for DTOs and enums.
- Error taxonomy and recovery guidance.
- Getting Started guide.
- Step-by-step integration tutorial.
- cURL examples.
- Architecture overview and Mermaid diagrams.
- Documentation strategy and validation notes.

Known limitations documented:

- No URL-based API versioning yet.
- No pagination for list endpoints yet.
- No explicit backend rate limiting yet.
- No mock server or contract tests yet.
