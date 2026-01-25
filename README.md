# InspectPro - Profile Management Service

Backend service for property inspection platform. Handles user authentication, professional profiles, credential verification, and subscription-based feature access.

## Tech Stack

- **Java 21** with Spring Boot 4.0.1
- **PostgreSQL 16** - Primary database
- **Redis 7** - Session management and caching
- **Docker & Docker Compose** - Containerization
- **JWT** - Authentication (15min access + 7day refresh tokens)
- **BCrypt** - Password hashing (cost factor 12)

## Quick Start (< 5 minutes)

### Prerequisites

- Docker & Docker Compose installed
- Port 8080, 5433, 6379 available

### Setup & Run

```bash
# Clone repository
git clone https://github.com/AngeloMancilla/InspectPro.git
cd InspectPro

# Start all services (PostgreSQL + Redis + App)
docker compose up -d

# Wait ~30 seconds for app to start
docker compose logs -f app

# Verify it's running
curl http://localhost:8080/api/v1/auth/register -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test1234","displayName":"Test User"}'
```

The application will be available at `http://localhost:8080`

## Interactive API Documentation

**Swagger UI** is available at: `http://localhost:8080/swagger-ui.html`

Features:
- Try all endpoints interactively
- View request/response schemas
- JWT authentication support (click "Authorize" button)

**Quick test:**
1. Go to `http://localhost:8080/swagger-ui.html`
2. Try `POST /api/v1/auth/register` to create a user
3. Copy the `accessToken` from response
4. Click "Authorize" button (top right) and paste token
5. Now you can test authenticated endpoints

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Create user + default BASIC profile
- `POST /api/v1/auth/login` - Login and get JWT tokens
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - Revoke refresh token

### Profiles
- `GET /api/v1/profiles/me` - Get current active profile
- `PUT /api/v1/profiles/me` - Update current profile
- `GET /api/v1/profiles` - List all user profiles
- `POST /api/v1/profiles` - Create new profile
- `POST /api/v1/profiles/switch/{profileId}` - Switch active profile context
- `POST /api/v1/profiles/{id}/upgrade` - Upgrade to VERIFIED_PROFESSIONAL
- `POST /api/v1/profiles/{id}/downgrade` - Downgrade to BASIC

### Credentials
- `POST /api/v1/credentials` - Submit credential for review
- `GET /api/v1/credentials` - List credentials for current profile
- `GET /api/v1/credentials/{id}` - Get credential details
- `POST /api/v1/credentials/{id}/approve` - Approve credential (admin)
- `POST /api/v1/credentials/{id}/reject` - Reject credential (admin)
- `DELETE /api/v1/credentials/{id}` - Delete credential

### Subscriptions
- `GET /api/v1/subscriptions/current` - Get active subscription
- `POST /api/v1/subscriptions/check-feature` - Check feature access
- `POST /api/v1/subscriptions/webhooks/stripe` - Stripe webhook handler

## Business Rules

### Profile Types
- **BASIC** - Default profile for all users
- **VERIFIED_PROFESSIONAL** - Requires ≥2 approved credentials

### Credential Lifecycle
- `PENDING` → `APPROVED` / `REJECTED` → `EXPIRED`
- Credentials auto-expire based on `expiresAt` date
- Grace period: VP keeps status if renewal is PENDING

### Scheduled Jobs
- **2:00 AM UTC** - Expire credentials past expiresAt date
- **3:00 AM UTC** - Downgrade VP profiles without active/pending credentials

## Architecture Decisions

See [DECISIONS.md](src/main/resources/docs/DECISIONS.md) for:
- Challenge A: Profile context switching (Redis-based solution)
- Challenge B: Credential expiration grace period
- Challenge C: Feature gating edge cases
- Database design rationale

## Local Development

### Run without Docker

```bash
# Start only PostgreSQL and Redis
docker compose up -d postgres redis

# Copy environment template
cp .env.example .env

# Run Spring Boot locally
./mvnw spring-boot:run
```

### Environment Variables

See [`.env.example`](.env.example) for all configuration options.

**Key variables:**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/inspectpro
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key-change-this-in-production
```

## Database Schema

```
users
├── id (PK)
├── email (unique)
├── password_hash
└── created_at

profiles
├── id (PK)
├── user_id (FK)
├── display_name
├── type (BASIC | VERIFIED_PROFESSIONAL)
└── created_at

credentials
├── id (PK)
├── profile_id (FK)
├── type (HVAC_LICENSE | EPA_CERTIFICATION | INSURANCE | STATE_LICENSE)
├── status (PENDING | APPROVED | REJECTED | EXPIRED)
├── expires_at
└── created_at

subscriptions
├── id (PK)
├── user_id (FK)
├── tier (BASIC | ENHANCED | PROFESSIONAL)
├── status
└── expires_at
```

## Testing

```bash
# Run tests
./mvnw test

# Build without tests
./mvnw clean package -DskipTests
```

## Project Structure

```
src/main/java/com/_9/inspect_pro/
├── controller/       # REST endpoints
├── service/          # Business logic
├── repository/       # JPA repositories
├── model/            # Domain entities
├── security/         # JWT & authentication
├── scheduler/        # Scheduled jobs
├── dto/              # Request/Response objects
├── exception/        # Custom exceptions
└── config/           # Configuration classes
```

## Troubleshooting

### App won't start
```bash
# Check logs
docker compose logs app

# Restart services
docker compose restart
```

### Port conflicts
```bash
# Stop all services
docker compose down

# Change ports in docker-compose.yml if needed
```

### Database connection issues
```bash
# Check PostgreSQL is healthy
docker compose ps postgres

# Reset database
docker compose down -v
docker compose up -d
```

## License

© 2025 - 309 Technology Inc
