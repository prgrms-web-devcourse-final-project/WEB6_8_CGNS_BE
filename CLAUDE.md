# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Korean Travel Guide Backend - A Spring Boot Kotlin application providing OAuth authentication, AI chatbot, user-to-user chat, and rating systems for connecting tourists with guides in Korea.

**Architecture**: Domain-Driven Design (DDD)
**Tech Stack**: Spring Boot 3.4.1, Kotlin 1.9.25, Spring AI 1.1.0-M2, PostgreSQL/H2

## Essential Commands

### Build & Run
```bash
# Run the application
./gradlew bootRun

# Build without tests
./gradlew build -x test

# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "WeatherServiceTest"

# Compile Kotlin only (faster than full build)
./gradlew compileKotlin
```

### Code Quality
```bash
# Check Kotlin code style (MUST pass before commit)
./gradlew ktlintCheck

# Auto-format Kotlin code
./gradlew ktlintFormat

# Setup git hooks (first time only)
./setup-git-hooks.sh      # Linux/Mac
setup-git-hooks.bat       # Windows
```

### Development
```bash
# Access Swagger UI
open http://localhost:8080/swagger-ui.html

# Access H2 Console (dev database)
open http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (empty)

# Health check
curl http://localhost:8080/actuator/health
```

### Local RabbitMQ Setup
```bash
# Start RabbitMQ with docker-compose (for WebSocket testing)
docker network create common
docker-compose up -d

# Check RabbitMQ status
docker logs rabbitmq-1

# Access RabbitMQ Management UI
open http://localhost:15672
# Username: admin
# Password: (from .env PASSWORD_1 or default: qwpokd153098)

# Stop RabbitMQ
docker-compose down
```

### Redis (Optional - for caching)
```bash
# Start Redis with Docker
docker run -d --name redis -p 6379:6379 redis:alpine

# Stop Redis
docker stop redis

# The app runs without Redis (session.store-type: none in dev)
```

## Architecture & Domain Structure

The codebase follows Domain-Driven Design with clear separation:

### Domain Packages
```
com/back/koreaTravelGuide/
├── common/                    # Shared infrastructure
│   ├── config/               # App, Security, Redis, AI, DevConfig
│   ├── security/             # OAuth2, JWT, filters
│   ├── exception/            # Global exception handler
│   └── ApiResponse.kt        # Standard API response wrapper
├── domain/
│   ├── user/                 # User management (GUEST, GUIDE, ADMIN)
│   ├── auth/                 # OAuth login endpoints
│   ├── ai/
│   │   ├── aiChat/          # AI chatbot sessions (Spring AI + OpenRouter)
│   │   ├── weather/         # Weather API integration (KMA)
│   │   └── tour/            # Tourism API integration
│   ├── userChat/            # WebSocket chat between Guest-Guide
│   │   ├── chatroom/        # Chat room management
│   │   ├── chatmessage/     # Message persistence & publishing
│   │   └── stomp/           # WebSocket config (Simple/Rabbit)
│   └── rate/                # Rating system for AI sessions & guides
```

### Key Architectural Patterns

1. **Each domain is self-contained** with its own entity, repository, service, controller, and DTOs
2. **Common utilities live in `common/`** - never duplicate config or security logic
3. **AI Chat uses Spring AI** with function calling for weather/tour tools
4. **User Chat uses WebSocket** with profile-based configuration:
   - **Dev**: SimpleBroker (in-memory, single server)
   - **Prod**: RabbitMQ STOMP Relay (scalable, multi-server)
5. **Port-Adapter pattern** for message publishing (`ChatMessagePublisher` interface with `SimpleChatMessagePublisher` and `RabbitChatMessagePublisher` implementations)
6. **Global exception handling** via `GlobalExceptionHandler.kt` - just throw exceptions, they're caught automatically

### Critical Configuration Files

- **build.gradle.kts**: Contains BuildConfig plugin that generates constants from YAML files (area-codes.yml, prompts.yml, etc.)
- **application.yml**: Dev config with H2, Redis optional, OAuth2 providers, RabbitMQ for local testing
- **application-prod.yml**: Production config with PostgreSQL, Redis required, RabbitMQ with connection stability settings
- **SecurityConfig.kt**: Currently allows all requests for dev (MUST restrict for production)
- **AiConfig.kt**: Spring AI ChatClient with OpenRouter (uses OPENROUTER_API_KEY env var)
- **DevConfig.kt**: Auto-generates 2 dummy GUIDE users on startup (dev profile only)

### Profile-Based Configuration Strategy

The application uses Spring profiles (`@Profile` annotation) to switch implementations:

**Development Profile (`dev`):**
- H2 in-memory database
- `SimpleChatMessagePublisher` - uses Spring's SimpleBroker (no RabbitMQ needed)
- `UserChatSimpleWebSocketConfig` - basic WebSocket with in-memory broker
- Redis optional (session.store-type: none)
- Dummy guide data auto-generation

**Production Profile (`prod`):**
- PostgreSQL database
- `RabbitChatMessagePublisher` - publishes to RabbitMQ
- `UserChatRabbitWebSocketConfig` - STOMP Broker Relay to RabbitMQ
- Redis required (session.store-type: redis)
- Connection stability settings (timeouts, heartbeats)

When adding new features that differ between dev/prod, follow this pattern:
1. Create an interface in the domain layer
2. Create separate implementations with `@Profile("dev")` and `@Profile("prod")`
3. Inject via the interface, Spring will wire the correct implementation

## Working with Spring AI

The AI chatbot uses Spring AI 1.1.0-M2 with **function calling** for weather and tour data:

```kotlin
// Tools are automatically called by AI when needed
// Located in: domain/ai/weather & domain/ai/tour
@Tool(description = "Get weather forecast")
fun getWeatherForecast(city: String): WeatherResponse

@Tool(description = "Get tourist spots")
fun getTourSpots(area: String): TourResponse
```

**Important**: System prompts are managed in `src/main/resources/prompts.yml` and compiled into BuildConfig at build time.

## WebSocket & Real-Time Messaging

### Architecture
User-to-user chat uses WebSocket with STOMP protocol. The implementation switches based on profile:

**Development**: Uses Spring's SimpleBroker (in-memory)
- Suitable for single-server development
- No external dependencies
- Messages stored in memory only

**Production**: Uses RabbitMQ STOMP Relay
- Scales across multiple server instances
- Messages persist in RabbitMQ
- Handles reconnection and failover

### Message Flow
1. Client connects to WebSocket endpoint: `/ws/userchat`
2. Client sends message to: `/pub/chat/send`
3. Server processes and publishes to: `/topic/chat/{roomId}`
4. `ChatMessagePublisher` interface abstracts the publishing mechanism
5. Messages are persisted to database via `ChatMessageService`

### RabbitMQ Configuration
Located in `application.yml` and `application-prod.yml`:
```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST}
    port: ${RABBITMQ_PORT}
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    stomp-port: ${RABBITMQ_STOMP_PORT}  # Default: 61613
```

RabbitMQ requires two plugins enabled:
- `rabbitmq_management` - Management UI (port 15672)
- `rabbitmq_stomp` - STOMP protocol support (port 61613)

## Testing Strategy

Tests are in `src/test/kotlin` mirroring the main structure:

- **Unit tests**: Mock services, test business logic
- **Integration tests**: Use `@SpringBootTest` with H2 in-memory database
- **Controller tests**: Use `@WebMvcTest` with MockMvc
- **Example**: `WeatherApiClientTest.kt` - uses MockMvc with mocked service layer

Always run `./gradlew ktlintCheck` before committing - it's enforced by git hooks.

## Security & Authentication

### OAuth2 Flow
1. User initiates OAuth (Google/Kakao/Naver)
2. `CustomOAuth2UserService` processes OAuth response
3. `CustomOAuth2LoginSuccessHandler` creates JWT tokens
4. JWT stored in HTTP-only cookie
5. `JwtAuthenticationFilter` validates requests

### JWT Configuration
- Access token: 60 minutes (configurable via `JWT_ACCESS_TOKEN_EXPIRATION_MINUTES`)
- Refresh token: 7 days (configurable via `JWT_REFRESH_TOKEN_EXPIRATION_DAYS`)
- Secret key: MUST set `CUSTOM__JWT__SECRET_KEY` in production

**Dev Mode**: All endpoints are currently permitAll() - restrict before deployment!

## Database Schema

- **Development**: H2 in-memory (jdbc:h2:mem:testdb), resets on restart
- **Production**: PostgreSQL (configured in application-prod.yml)
- **JPA Strategy**: `ddl-auto: create-drop` in dev (wipes DB on restart), `update` in prod

Main entities:
- `User` - OAuth users with roles (GUEST/GUIDE/ADMIN)
- `AiChatSession` / `AiChatMessage` - AI conversation history
- `ChatRoom` / `ChatMessage` - User-to-user messaging
- `Rate` - Ratings for AI sessions or guides

## Environment Variables

Required `.env` file (copy from .env.example):
```bash
# AI (Required)
OPENROUTER_API_KEY=sk-or-v1-...
OPENROUTER_MODEL=z-ai/glm-4.5-air:free

# OAuth (Required for auth)
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
KAKAO_CLIENT_ID=...
KAKAO_CLIENT_SECRET=...
NAVER_CLIENT_ID=...
NAVER_CLIENT_SECRET=...

# APIs (Required for AI tools)
WEATHER__API__KEY=...
TOUR_API_KEY=...

# JWT (Required for production)
CUSTOM__JWT__SECRET_KEY=...

# RabbitMQ (Required for prod WebSocket)
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=qwpokd153098
RABBITMQ_STOMP_PORT=61613

# Redis (Optional in dev, required in prod)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

## API Response Standards

All controllers return `ApiResponse<T>`:
```kotlin
// Success
ApiResponse(msg = "Success", data = userDto)
// Returns: {"msg": "Success", "data": {...}}

// Error (handled automatically)
throw IllegalArgumentException("Invalid input")
// Returns: {"msg": "Invalid input", "data": null}
```

Common exceptions mapped to HTTP status:
- `IllegalArgumentException` → 400 Bad Request
- `NoSuchElementException` → 404 Not Found
- `AccessDeniedException` → 403 Forbidden
- Others → 500 Internal Server Error

## Development Workflow

1. **Create branch**: `{type}/{scope}/{issue-number}` (e.g., `feat/be/42`)
2. **Make changes**: Follow DDD structure - put code in correct domain
3. **Format code**: `./gradlew ktlintFormat`
4. **Test**: `./gradlew test`
5. **Commit**: `{type}(scope): summary` (e.g., `feat(be): Add weather caching`)
6. **PR title**: `{type}(scope): summary (#{issue})` (e.g., `feat(be): Add weather caching (#42)`)

## Infrastructure & Deployment

### Terraform Infrastructure (`infra/main.tf`)
EC2 instance setup with:
1. Docker & docker-compose installation
2. Container network (`common`)
3. Nginx Proxy Manager (ports 80, 443, 81)
4. Redis (port 6379)
5. PostgreSQL 16 (port 5432)
6. RabbitMQ with management & STOMP plugins (ports 5672, 15672, 61613)

**Order matters**: Docker → docker-compose → network → containers

### CI/CD Pipeline (`.github/workflows/deploy.yml`)
Blue-Green deployment strategy:
1. Build on GitHub Actions runner
2. Transfer JAR to EC2
3. Deploy to blue/green container based on availability
4. Health check before switching traffic
5. Environment variables injected via `docker run -e`

### Docker Compose Files
- `docker-compose.yml` (root) - Local development RabbitMQ
- `infra/rabbitmq-docker-compose.yml` - EC2 production RabbitMQ template

Both require `common` network to be pre-created: `docker network create common`

## Common Issues & Solutions

### Build fails with "BuildConfig not found"
- Run `./gradlew build` to generate BuildConfig.kt from YAML files
- BuildConfig is gitignored - regenerates on every build

### Redis connection errors
- Redis is optional in dev (session.store-type: none)
- Start Redis: `docker run -d -p 6379:6379 --name redis redis:alpine`
- Check: `docker logs redis`

### RabbitMQ connection issues
- In dev: RabbitMQ is optional (SimpleBroker used instead)
- In prod: RabbitMQ is required for WebSocket
- Start local RabbitMQ: `docker-compose up -d`
- Check logs: `docker logs rabbitmq-1`
- Verify plugins: `rabbitmq_management` and `rabbitmq_stomp` must be enabled

### ktlint failures
- Auto-fix: `./gradlew ktlintFormat`
- Pre-commit hook enforces this - setup via `./setup-git-hooks.sh`

### Spring AI errors
- Verify `OPENROUTER_API_KEY` in .env
- Check model name matches OpenRouter API (currently: z-ai/glm-4.5-air:free)
- Logs show AI requests: `logging.level.org.springframework.ai: DEBUG`

### OAuth login fails
- Ensure all OAuth credentials in .env
- Check redirect URIs match OAuth provider settings
- Dev: `http://localhost:8080/login/oauth2/code/{provider}`

### WebSocket connection fails
- Check profile: dev uses SimpleBroker, prod uses RabbitMQ
- In prod: Ensure RabbitMQ is running and accessible
- Verify STOMP port (61613) is open and reachable
- Check `UserChatStompAuthChannelInterceptor` for authentication issues

## Important Notes

- **Never commit .env** - it's gitignored, contains secrets
- **ktlint is enforced** - setup git hooks on first clone
- **H2 data is ephemeral** - resets on every restart in dev
- **Global config in common/** - don't duplicate security/config in domains
- **BuildConfig is generated** - don't edit manually, modify YAML sources
- **Redis is optional in dev** - required for production caching/sessions
- **RabbitMQ is optional in dev** - required for production WebSocket scaling
- **Profile-based beans** - use `@Profile` to switch implementations between dev/prod
- **Dummy data in dev** - 2 guide users auto-generated on startup (DevConfig.kt)
- **Docker network required** - `common` network must exist before running docker-compose
- **Terraform order matters** - Docker installation before docker-compose, containers after both
