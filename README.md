# Smart Task Manager

A secure, production-ready Spring Boot task management backend with JWT auth, board collaboration, labels, reminders, activity history, advanced search, migrations, and containerized deployment.

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Flyway migrations
- OpenAPI/Swagger (`/swagger-ui.html`)
- Testcontainers + MockMvc tests

## Architecture
- **Controllers**: versioned REST endpoints under `/api/v1`
- **Service layer**: business logic, authorization checks, and orchestration
- **DTO mapping**: controllers return DTOs only (no direct JPA entity exposure)
- **Repositories**: optimized aggregate/search queries + pagination
- **Global exception handler**: consistent JSON error contract

## Security Notes
- JWT secret and DB credentials are environment driven.
- CORS origins are configured via `APP_CORS_ALLOWED_ORIGINS`.
- Authorization rules enforced for board/list/task/comment operations:
  - Board owner/member access
  - Owner/admin privileged mutations
  - Assignee must belong to board owner/member set
  - Comment delete allowed for author or board owner/admin

## Profiles & Configuration
### Development (default)
Uses `application-dev.properties` with env overrides:
- `DB_URL` (default `jdbc:postgresql://localhost:5432/taskmanager`)
- `DB_USERNAME` (default `admin`)
- `DB_PASSWORD` (default `dev-local-only-UseStrongPassword!2026`)
- `JWT_SECRET` (optional override)
- `APP_CORS_ALLOWED_ORIGINS` (comma-separated)

### Production
Set `SPRING_PROFILES_ACTIVE=prod` and provide:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`

SQL logging is disabled in prod config.

## API Documentation
- OpenAPI JSON: `/api/docs`
- Swagger UI: `/swagger-ui.html`

## Key API Areas
- `POST /api/v1/auth/register`, `POST /api/v1/auth/login`
- `GET/POST/PUT/DELETE /api/v1/boards`
- `POST/DELETE /api/v1/boards/{id}/members/...`
- `GET /api/v1/boards/{id}/activities`
- `GET/POST/PUT/DELETE /api/v1/boards/{boardId}/lists`
- `GET/POST/PUT/DELETE /api/v1/tasks` (+ filters, search, pagination)
- `GET /api/v1/tasks/overdue`, `GET /api/v1/tasks/reminders`
- `GET/POST/DELETE /api/v1/tasks/{taskId}/comments`
- `GET /api/v1/dashboard/{boardId}`

## Local Setup
1. Start DB:
   ```bash
   docker compose up -d postgres
   ```
2. Run app:
   ```bash
   ./mvnw spring-boot:run
   ```

## Testing
```bash
./mvnw test
```
Tests use `test` profile with Testcontainers-backed PostgreSQL.

## Build & Deploy
### Build jar
```bash
./mvnw clean package
```

### Build Docker image
```bash
docker build -t smart-task-manager:latest .
```

### Run container
```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host:5432/taskmanager \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=your-secure-database-password \
  -e JWT_SECRET=replace-with-strong-secret \
  -e APP_CORS_ALLOWED_ORIGINS=https://your-frontend.example.com \
  smart-task-manager:latest
```

## Health Checks
- Liveness/readiness endpoint: `/actuator/health`

## Frontend Integration
Set `APP_CORS_ALLOWED_ORIGINS` to the frontend URL(s), e.g.:
- `http://localhost:5173`
- `https://app.example.com`

## CI
GitHub Actions workflow at `.github/workflows/ci.yml` runs tests and Docker build on pushes/PRs.
