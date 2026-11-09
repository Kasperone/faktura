# Faktura

Monorepo for the Faktura invoicing application (Poland-focused, KSeF-ready roadmap).

## Why this project

Faktura is my hands-on Java learning project. Coming from a frontend background, I'm expanding toward full-stack development with Java, Spring Boot, and React. I chose an invoicing application to explore backend topics such as data modeling, validation, persistence, security, and external integrations. The project is at an early stage, and its scope will grow as I learn.

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

GitHub Actions runs **gitleaks**, backend `./mvnw -B verify` (tests, Spotless, ArchUnit; Postgres service), and frontend `npm ci` / lint / build. See [AGENTS.md](AGENTS.md) and [docs/testing.md](docs/testing.md).

## License

[MIT](LICENSE)
