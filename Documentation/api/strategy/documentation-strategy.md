# Documentation Strategy

This document explains how the WeatherWear API documentation is maintained and how it maps to diploma evaluation criteria.

## Goals

- Give developers enough information to authenticate and call the API.
- Keep request and response contracts traceable to Java DTOs.
- Provide a version-controlled OpenAPI specification.
- Document known limitations clearly instead of hiding them.
- Support both human-readable guides and machine-readable API tooling.

## Standards Used

| Area | Standard or tool |
| --- | --- |
| API style | REST over HTTP |
| API specification | OpenAPI 3.0.3 |
| Auth scheme | HTTP Bearer JWT |
| Data format | JSON |
| Diagrams | Mermaid |
| Runtime docs | Springdoc Swagger UI |
| Runtime annotations | `io.swagger.v3.oas.annotations` on controllers and DTOs |
| Static docs | Markdown documentation repository |

## Source of Truth

| Artifact | Role |
| --- | --- |
| Java controllers | Runtime endpoint paths and HTTP methods |
| Java DTOs | Request and response fields |
| Swagger/OpenAPI annotations | Runtime summaries, descriptions, status codes, examples, and schema metadata |
| Validation annotations | Required fields and validation constraints |
| `GlobalExceptionHandler` | Error response structure and status code mapping |
| `SecurityConfig` | Public vs protected endpoint rules |
| OpenAPI file | Version-controlled API contract for documentation and review |

## Naming Conventions

- File and folder names use lowercase kebab-case where new files are created.
- Endpoint paths use plural resource names where already defined by the backend: `/recommendations`, `/preferences`, `/history`, `/feedback`, `/chat`.
- JSON field names follow the existing Java DTO field names as serialized by Jackson, for example `feelsLike`, `recommendationHistoryId`, and `createdAt`.
- OpenAPI operation IDs use verb-noun style, for example `createRecommendation` and `getChatSessions`.

## Folder Convention

```text
Documentation/api/
  openapi/
  reference/
  guides/
  examples/
  architecture/
  strategy/
```

This structure separates machine-readable contracts, endpoint reference, tutorials, examples, architecture, and maintenance rules.

## Versioning Approach

The API is documented as version `1.0.0`. Since the current backend does not use URL versioning, compatibility is managed through OpenAPI versioning, Git history, and `changelog.md`.

Breaking changes should be handled by:

- Creating a new API version in OpenAPI.
- Updating guides and examples in the same pull request.
- Marking old endpoints as deprecated before removal.
- Adding migration notes to `changelog.md`.

## Formatting Rules

- Use Markdown headings in a predictable hierarchy.
- Keep endpoint examples runnable with `curl`.
- Use JSON blocks for all request and response examples.
- Use tables for endpoint summaries, models, status codes, and requirement coverage.
- Do not include secrets, real JWTs, real API keys, or production credentials.

## Maintenance Workflow

1. Change controller, DTO, service, security, or exception behavior.
2. Update Swagger annotations in controllers and DTOs.
3. Update `openapi/weatherwear-api.openapi.json`.
4. Update affected reference pages and examples.
5. Add a changelog entry.
6. Run local validation from `strategy/validation.md`.
7. Review that examples match the actual API behavior.

## Known Documentation Gaps

| Gap | Reason | Planned improvement |
| --- | --- | --- |
| No live hosted docs site in repository | Current deliverable is Markdown plus runtime Swagger UI | Publish Redoc or Docusaurus from this folder |
| No generated mock server | Not configured in the backend | Add Prism or Redocly mock server in future |
| No contract tests | Current tests are Java unit and controller tests | Add Pact or OpenAPI-based contract checks |
| No pagination docs for implemented behavior | Pagination is not implemented | Add when list endpoints accept page and size parameters |
| No rate-limit headers | Rate limiting is not implemented | Add once API gateway or filter is introduced |
