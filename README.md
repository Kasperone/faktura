# Faktura

Monorepo for the Faktura invoicing application (Poland-focused, KSeF-ready roadmap).

## Stack

| Area     | Technology                          |
| -------- | ----------------------------------- |
| Backend  | Java 25, Spring Boot 4.1, Maven     |
| Frontend | React, TypeScript, Vite             |
| Database | PostgreSQL 18 (Docker Compose only) |

## Prerequisites

- Java 25 JDK
- Node.js 22+ and npm
- Docker (for PostgreSQL)

## Local development

### 1. Start PostgreSQL

```bash
docker compose up -d
```

Defaults: database `faktura`, user/password `faktura`, port `5432`. Copy `.env.example` to `.env` if you want to override connection settings.

### 2. Backend API

```bash
cd backend
./mvnw spring-boot:run
```

- API base: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`
- Ping: `http://localhost:8080/api/v1/ping`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

App: `http://localhost:5173` (Vite proxies `/api` to the backend).

### Useful commands

```bash
# Backend tests
cd backend && ./mvnw verify

# Frontend lint and production build
cd frontend && npm run lint && npm run build
```

## Repository layout

```
backend/     Spring Boot REST API (package-by-feature under io.github.kasperone.faktura.*)
frontend/    React SPA
docker-compose.yml   PostgreSQL 18 only (application runs on the host for now)
```

## CI

GitHub Actions runs Maven `verify` for the backend (with a Postgres service) and `npm ci`, lint, and build for the frontend on pushes and pull requests.
