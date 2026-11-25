# Agent guide (Faktura)

This file is for humans and coding agents working in the repo. **Maven and CI enforce quality; Markdown guides only** (nothing under `docs/` auto-loads in Cursor CLI unless you open it).

## Verify before push (match CI)

```bash
docker compose up -d                                    # Postgres for JDBC (local run only)
cd backend && ./mvnw -B verify                        # tests, Spotless, ArchUnit
cd frontend && npm ci && npm run lint && npm run build
# secrets (optional locally): see docs/testing.md — gitleaks CLI not required on PATH if you use Docker
```

With Postgres down, `FakturaApplicationTests` still passes (`application-test.yml` disables datasource auto-config). `PostgresFlywayContextTest` starts its own PostgreSQL 18 container via Testcontainers and needs a working Docker daemon. CI runs that test on the GitHub runner and also starts a Postgres 18 service.

## Hard constraints

| Rule | Enforced by |
|------|-------------|
| Java 25, Spring Boot 4.1, `groupId` `io.github.kasperone`, base package `io.github.kasperone.faktura` | **advisory** (review) |
| Production DB: PostgreSQL via env vars — no H2 | **advisory** (`pom.xml`, `application.yml`) |
| Flyway owns the schema; Hibernate `ddl-auto` is `validate`; `open-in-view` is `false` | **`application.yml`**, `PostgresFlywayContextTest` |
| `./mvnw verify` includes Spotless **check** (Google Java Format) | **`spotless-maven-plugin`** |
| Feature package boundary rules (ArchUnit) | **`ArchitectureRulesTest`** (`mvn verify`) |
| No committed secrets | **CI `gitleaks` job** |
| Frontend ESLint + production build | **CI `frontend` job** |

## Repository map

- `backend/` — single Maven module API
- `frontend/` — Vite React SPA
- `docs/architecture.md` — layout and layering
- `docs/invariants.md` — planned vs enforced rules
- `docs/testing.md` — gates, local commands, secret scan
- `docs/adr/` — architecture decision records

## Advisory (not gated)

- Package-by-feature folders (`customer`, `invoice`, `organization`, `ksef`) are mostly placeholders today.
- Later phases: auth, KSeF, PDF (see README and `docs/invariants.md`). Flyway and the Testcontainers Postgres test are in place.
- Do not force-push, rewrite merged history, or commit `.env` secrets.

When docs and code disagree, **code + CI win**; fix docs in the same change.

## Library APIs (Context7)

`.cursor/mcp.json` registers the `context7` MCP server (OAuth: `https://mcp.context7.com/mcp/oauth`). Sign in once under Cursor Settings → Tools & MCP.

Before introducing or changing a third-party call, annotation, or config key, resolve that library and query its docs for the version in `backend/pom.xml` or `frontend/package.json` (`resolve-library-id`, then `query-docs`). Skip renames, formatting, and edits that keep the same library calls. `./mvnw verify` and the frontend lint/build remain the check that the code is valid. The same steps are in `.cursor/rules/context7-library-docs.mdc`.

## Cursor CLI note

Only this `AGENTS.md` is the documented entry point for CLI sessions. `.cursor/rules`, hooks, and skills do **not** auto-apply unless you configure them separately.
