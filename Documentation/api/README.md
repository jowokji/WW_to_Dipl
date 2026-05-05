# WeatherWear API Documentation

Reviewed: 2026-05-04

This folder contains the API documentation set for the WeatherWear diploma backend. It is based on the current Spring Boot source code in `src/main/java/com/weatherwear` and the runtime configuration in `src/main/resources/application.yml`.

## API Scope

WeatherWear is a REST API that helps authenticated users receive clothing recommendations based on current weather, personal style preferences, and optional occasion context. The API also stores recommendation history, collects feedback, and provides an AI chat assistant.

Base URL for local development:

```text
http://localhost:8090/api
```

Production base URL from `application-prod.yml`:

```text
https://wwtodipl-production.up.railway.app/api
```

Runtime Swagger UI:

```text
http://localhost:8090/api/swagger-ui.html
```

Runtime generated API docs:

```text
http://localhost:8090/api/api-docs
```

Version-controlled OpenAPI specification:

```text
Documentation/api/openapi/weatherwear-api.openapi.json
```

The runtime Swagger UI is enriched with controller-level OpenAPI annotations, DTO schema descriptions, request examples, response examples, error examples, and endpoint-specific JWT security metadata.

## What Is Included

| Area | Included endpoints | Authentication |
| --- | --- | --- |
| Authentication | Register, login | Public |
| Account | Delete current account and associated user-owned data | JWT required |
| Weather | Lookup by city, lookup by coordinates | JWT required |
| Recommendations | Generate AI clothing recommendation | JWT required |
| Preferences | Read, create, update user preferences | JWT required |
| History | List, read, clear recommendation history | JWT required |
| Feedback | Create, list, filter, delete recommendation feedback | JWT required |
| Chat | Send message, list sessions, read messages, delete session | JWT required |
| Health | Service status | Public |

## Folder Structure

| Folder | Purpose |
| --- | --- |
| `openapi/` | Version-controlled OpenAPI 3.0.3 specification |
| `reference/` | Endpoint reference, data models, errors, limits, versioning |
| `guides/` | Getting started, authentication, step-by-step integration tutorial |
| `examples/` | Runnable cURL-style examples and sample responses |
| `architecture/` | System context, component diagram, request lifecycle, sequence diagrams, and PNG exports |
| `strategy/` | Documentation strategy, validation notes, changelog |

## Requirement Coverage

| Diploma requirement | Coverage in this documentation set |
| --- | --- |
| Structured API reference | `reference/endpoints.md` |
| API specification using an industry standard | `openapi/weatherwear-api.openapi.json` |
| Endpoint examples | OpenAPI examples plus `examples/curl-examples.md` |
| Request and response formats | OpenAPI schemas plus `reference/data-models.md` |
| Authentication and authorization | `guides/authentication.md` |
| Error documentation | `reference/error-taxonomy.md` |
| Getting Started guide | `guides/getting-started.md` |
| Step-by-step integration tutorial | `guides/integration-tutorial.md` |
| Architecture overview | `architecture/overview.md` and `.mmd` diagrams |
| Component/dependency diagram | `architecture/component-diagram.mmd` embedded in `architecture/overview.md` |
| Request-response lifecycle | `architecture/request-lifecycle.mmd` embedded in `architecture/overview.md` |
| PNG diagram exports | `architecture/png/*.png` embedded in `architecture/overview.md` |
| Documentation strategy | `strategy/documentation-strategy.md` |
| Versioning and changelog | `reference/versioning-and-limits.md`, `strategy/changelog.md` |
| Validation report | `strategy/validation.md` |
| AI safety and prompt documentation | `../ai_safety.md` |

## Known Gaps

The current backend does not implement pagination, explicit rate limiting, refresh tokens, API keys, or URL-based API versioning. These topics are documented as current limitations and future evolution points.
