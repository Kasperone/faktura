# Architecture (phase 0)

## Monorepo

| Part | Role |
|------|------|
| `backend/` | Spring Boot 4.1 REST API (`faktura-api`) |
| `frontend/` | React + TypeScript + Vite SPA |
| `docker-compose.yml` | PostgreSQL 18 for local dev (app not containerized) |

## Backend layout (package-by-feature)

Base package: `io.github.kasperone.faktura`.

```
io.github.kasperone.faktura
├── FakturaApplication.java      # bootstrap (root package)
├── config/                      # cross-cutting Spring config (e.g. CORS)
├── shared/                      # shared technical adapters (e.g. api ping)
│   └── api/
├── customer/                    # buyer — placeholder (package-info only)
├── invoice/                     # invoices — placeholder
├── organization/                # seller profile — placeholder
└── ksef/                        # KSeF integration — placeholder
```

**As implemented today:** only `config`, `shared.api`, and the application class contain code; feature packages are empty shells ready for phase 1.

**Enforced today (ArchUnit, `ArchitectureRulesTest`):** `config` and `shared` must not depend on feature packages; feature slices must not depend on each other (cross-feature rules use `allowEmptyShould(true)` until feature code beyond `package-info` exists).

**Intended dependency direction (advisory beyond ArchUnit):**

- Feature packages (`customer`, `invoice`, `organization`, `ksef`) must not depend on each other.
- `config` must not depend on feature packages.
- `shared` must not depend on feature packages.
- Features may later depend on `shared` and Spring/JDK types (not enforced until code exists).

No hexagonal modules, no multi-module Maven layout, no Spring Modulith.

## API surface (current)

- `GET /api/v1/ping` — health shell
- `GET /actuator/health` — actuator (DB health when datasource is active)

## Frontend

Empty shell layout; dev server proxies `/api` to `http://localhost:8080` (`vite.config.ts`).
