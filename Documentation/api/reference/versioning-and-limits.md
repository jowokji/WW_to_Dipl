# Versioning, Pagination, Rate Limits, and Idempotency

This page documents current behavior and planned evolution points for advanced API design topics.

## Versioning Strategy

Current API version:

```text
1.0.0
```

The current backend does not include a URL version segment such as `/v1`. Versioning is managed through:

- The OpenAPI `info.version` field.
- Git history for the OpenAPI file and documentation.
- `strategy/changelog.md` for human-readable changes.
- Backward-compatible DTO changes where possible.

Recommended future strategy:

- Keep compatible additive changes under `1.x`.
- Introduce `/api/v2` only for breaking contract changes.
- Maintain old and new specifications during transition.
- Deprecate endpoints in OpenAPI before removing them.

## Pagination Strategy

Current state:

- `GET /history`
- `GET /feedback`
- `GET /chat/sessions`
- `GET /chat/sessions/{id}`

These endpoints return full lists owned by the current user. Pagination is not implemented in the current codebase.

Recommended future contract:

```text
GET /history?page=0&size=20&sort=createdAt,desc
```

Recommended paginated response shape:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 128,
  "totalPages": 7
}
```

## Rate Limits

Current state:

- No explicit rate limiting middleware is implemented.
- External provider limits may still apply for OpenWeather and the LLM API.

Recommended future headers:

```http
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 42
X-RateLimit-Reset: 1714483200
```

Recommended policy:

- Lower limits for AI-heavy endpoints: `/recommendations` and `/chat`.
- Higher limits for read endpoints: `/preferences`, `/history`, `/feedback`.
- Return `429 Too Many Requests` with retry guidance when limits are exceeded.

## Idempotency

Current behavior:

| Endpoint type | Idempotency |
| --- | --- |
| GET endpoints | Idempotent |
| PUT `/preferences` | Intended to be idempotent for the same payload |
| DELETE `/history` | Idempotent from a client perspective |
| DELETE `/feedback/{feedbackId}` | First call deletes, later calls may return `404` |
| DELETE `/chat/sessions/{id}` | First call deletes, later calls may return `404` |
| POST `/auth/register` | Not idempotent because duplicate email returns `409` |
| POST `/recommendations` | Not idempotent because it creates history and calls LLM |
| POST `/feedback` | Not idempotent because it creates feedback rows |
| POST `/chat` | Not idempotent because it creates messages and may create a session |

Recommended future enhancement:

- Support `Idempotency-Key` for AI and feedback creation endpoints.
- Store key, user ID, request hash, response hash, and expiration.
- Return the original response for safe retries.

