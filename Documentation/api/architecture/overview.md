# Architecture Overview

WeatherWear is a Spring Boot REST backend. It combines user authentication, weather data, AI-generated clothing recommendations, persistence, and feedback loops.

## Diagram Coverage

| Requirement area | Diagram in this document | Purpose |
| --- | --- | --- |
| System context | System Context | Shows external actors and dependencies |
| Modules and resources | Component Diagram, Main Modules, Resource Model | Explains backend structure and ownership boundaries |
| Complex request flows | Recommendation Request Flow, Chat Request Flow | Shows step-by-step API workflows |
| Request-response lifecycle | Request-Response Lifecycle | Shows validation, authentication, dependencies, persistence, and errors |
| Dependency map | Component Diagram | Shows internal and external dependencies |

## System Context

```mermaid
flowchart LR
    Client["Mobile or web client"] --> API["WeatherWear REST API"]
    API --> DB["PostgreSQL"]
    API --> Weather["OpenWeather API"]
    API --> LLM["LLM API"]
    Dev["Developer"] --> Swagger["Swagger UI and OpenAPI docs"]
    Swagger --> API
```

Source file:

```text
Documentation/api/architecture/system-context.mmd
```

## Main Modules

| Module | Java package | Responsibility |
| --- | --- | --- |
| Controllers | `controller` | HTTP routing, request validation, response status selection |
| DTOs | `dto` | API request and response contracts |
| Services | `service` | Business logic and orchestration |
| Security | `security`, `config.SecurityConfig` | JWT validation, current user resolution, stateless authorization |
| Persistence | `entity`, `repository` | PostgreSQL-backed application data |
| Weather client | `client.weather` | OpenWeather integration and normalization |
| LLM client | `client.llm` | AI recommendation generation |
| Exceptions | `exception` | Error taxonomy and global error response formatting |

## Component Diagram

```mermaid
flowchart TD
    Client["Mobile or web client"] --> Security["Spring Security + JwtAuthFilter"]
    Security --> Controllers["REST Controllers"]

    Controllers --> AuthService["AuthService"]
    Controllers --> UserService["UserService"]
    Controllers --> WeatherService["WeatherService"]
    Controllers --> RecommendationService["RecommendationService"]
    Controllers --> PreferenceService["PreferenceService"]
    Controllers --> HistoryService["HistoryService"]
    Controllers --> FeedbackService["FeedbackService"]
    Controllers --> ChatService["ChatService"]

    RecommendationService --> WeatherService
    RecommendationService --> LlmClient["LlmClient"]
    RecommendationService --> Repositories["Spring Data JPA repositories"]

    ChatService --> WeatherService
    ChatService --> LlmClient
    ChatService --> Repositories

    WeatherService --> WeatherClient["WeatherApiClient"]
    WeatherService --> WeatherCacheRepository["WeatherCacheRepository"]

    AuthService --> JwtService["JwtService"]
    AuthService --> UserRepository["UserRepository"]
    UserService --> UserRepository
    PreferenceService --> Repositories
    HistoryService --> Repositories
    FeedbackService --> Repositories

    Repositories --> PostgreSQL["PostgreSQL"]
    WeatherCacheRepository --> PostgreSQL
    WeatherClient --> OpenWeather["OpenWeather API"]
    LlmClient --> ExternalLLM["LLM API"]

    Controllers --> DTOs["Request and response DTOs"]
    Exceptions["GlobalExceptionHandler"] --> ErrorResponse["ErrorResponse DTO"]
```

Source file:

```text
Documentation/api/architecture/component-diagram.mmd
```

## Resource Model

| Resource | Meaning |
| --- | --- |
| User | Account, email, BCrypt password hash, role |
| User preference | Style, sensitivity, activity, and clothing constraints |
| Weather cache | Cached OpenWeather response snapshot for 30 minutes |
| Recommendation history | Saved AI recommendation linked to a user |
| Feedback | User evaluation of a recommendation |
| Chat session | Conversation container for AI style assistant |
| Chat message | User or assistant message inside a session |

## Recommendation Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant API as RecommendationController
    participant Auth as JwtAuthFilter
    participant Weather as WeatherService
    participant Cache as WeatherCacheRepository
    participant LLM as LlmClient
    participant DB as PostgreSQL

    Client->>Auth: POST /api/recommendations with Bearer token
    Auth->>API: Authenticated request
    API->>Weather: getWeather(city or coordinates)
    Weather->>Cache: find non-expired cached weather
    alt cache hit
        Cache-->>Weather: cached weather
    else cache miss
        Weather->>Weather: call OpenWeather API
        Weather->>Cache: save weather cache row
    end
    API->>DB: load current user preferences
    API->>LLM: generate clothing recommendation
    API->>DB: save recommendation history
    API-->>Client: 200 RecommendationResponse
```

Source file:

```text
Documentation/api/architecture/recommendation-sequence.mmd
```

## Chat Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant API as ChatController
    participant Service as ChatService
    participant DB as PostgreSQL
    participant Weather as WeatherService
    participant LLM as LlmClient

    Client->>API: POST /api/chat with Bearer token
    API->>Service: sendMessage(request)
    Service->>DB: get or create chat session
    Service->>DB: save USER message
    opt city supplied
        Service->>Weather: fetch current weather context
    end
    Service->>DB: load last 10 messages
    Service->>LLM: generate assistant answer
    Service->>DB: save ASSISTANT message and update session
    Service-->>API: ChatResponse
    API-->>Client: 200 ChatResponse
```

Source file:

```text
Documentation/api/architecture/chat-sequence.mmd
```

## Request Lifecycle

1. Client sends HTTP request to `/api/...`.
2. Spring Security checks whether the path is public or protected.
3. `JwtAuthFilter` validates JWT for protected endpoints.
4. Controller validates query parameters, path variables, and JSON request body.
5. Service executes business logic and calls repositories or external clients.
6. Repository persists or reads PostgreSQL data.
7. External calls to OpenWeather or LLM provider are wrapped in domain exceptions.
8. Controller returns DTO response, or `GlobalExceptionHandler` returns `ErrorResponse`.

## Request-Response Lifecycle

```mermaid
flowchart TD
    Start["Client sends HTTP request to /api/..."] --> PublicCheck{"Public endpoint?"}
    PublicCheck -- "Yes" --> Controller["Controller receives request"]
    PublicCheck -- "No" --> Jwt["JwtAuthFilter validates Bearer token"]
    Jwt --> JwtValid{"Token valid?"}
    JwtValid -- "No" --> Unauthorized["401 Unauthorized"]
    JwtValid -- "Yes" --> Controller

    Controller --> Validate["Validate query, path, and JSON body"]
    Validate --> ValidRequest{"Valid request?"}
    ValidRequest -- "No" --> BadRequest["400 Bad Request with ErrorResponse"]
    ValidRequest -- "Yes" --> Service["Service executes business workflow"]

    Service --> External{"External dependency needed?"}
    External -- "Weather" --> OpenWeather["OpenWeather API"]
    External -- "LLM" --> LLM["LLM API"]
    External -- "No" --> Persistence["Read or write PostgreSQL"]

    OpenWeather --> DependencyOk{"Dependency success?"}
    LLM --> DependencyOk
    DependencyOk -- "No" --> BadGateway["502 Bad Gateway with ErrorResponse"]
    DependencyOk -- "Yes" --> Persistence

    Persistence --> ResourceCheck{"Resource exists and belongs to user?"}
    ResourceCheck -- "No" --> NotFound["404 Not Found with ErrorResponse"]
    ResourceCheck -- "Yes" --> Success["2xx response DTO"]
```

Source file:

```text
Documentation/api/architecture/request-lifecycle.mmd
```

## Cross-Service Contract Notes

The project is a single backend service with external provider integrations, not a multi-service microservice system. The main external contracts are:

- OpenWeather API response contract, normalized into `WeatherResponse`.
- LLM provider chat completion contract, normalized into plain recommendation text.
- PostgreSQL schema contract, documented separately in `Documentation/database_documentation.md`.
