# Authentication Guide

WeatherWear uses stateless JWT authentication.

## Public Endpoints

The following endpoints do not require a token:

- `POST /auth/register`
- `POST /auth/login`
- `GET /health`
- `/swagger-ui/**`
- `/api-docs/**`

All other application endpoints require a JWT.

## Register

```bash
curl -X POST "http://localhost:8090/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.example.signature",
  "email": "alex@example.com",
  "role": "USER"
}
```

## Login

```bash
curl -X POST "http://localhost:8090/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"alex@example.com\",\"password\":\"securePass123\"}"
```

## Use the Token

Send the token in the `Authorization` header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.example.signature
```

Example:

```bash
curl "http://localhost:8090/api/preferences" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.example.signature"
```

## Token Contents

The JWT contains:

| Claim | Meaning |
| --- | --- |
| `sub` | User email |
| `role` | User role, for example `USER` |
| `iat` | Issued-at timestamp |
| `exp` | Expiration timestamp |

The default expiration from application configuration is `86400000` milliseconds, which is 24 hours.

## Security Rules

- Never store plaintext passwords. The backend stores BCrypt hashes.
- Never commit JWT secrets to Git.
- Use a strong `JWT_SECRET` in `.env`, CI/CD secrets, or production environment variables.
- Treat JWTs as bearer credentials. Anyone with the token can call protected endpoints until it expires.
- Prefer HTTPS in deployed environments.
- Avoid logging `Authorization` headers and passwords.

