# Testing and quality gates

## Backend (`backend/`)

| Gate | Command | Phase |
|------|---------|-------|
| Unit / MVC tests | `./mvnw test` | `test` |
| Spotless | `./mvnw spotless:check` | `verify` |
| ArchUnit | `./mvnw test` (`ArchitectureRulesTest`) | `test` |
| PostgreSQL 18 context + Flyway | `./mvnw test` (`PostgresFlywayContextTest`, needs Docker) | `test` |
| Package + Spring Boot repackage | `./mvnw verify` | `verify` |

Fix formatting:

```bash
cd backend && ./mvnw spotless:apply
```

`FakturaApplicationTests` and the MVC slice use the `test` profile. That profile excludes datasource auto-config, so those tests pass with Postgres down.

`PostgresFlywayContextTest` does not use the `test` profile. It starts `postgres:18` through Testcontainers and connects with `@ServiceConnection`. Flyway then applies `src/main/resources/db/migration`. Docker must be reachable; the test does not use the Compose database or the CI service container.

CI runs `./mvnw -B verify` on a GitHub-hosted runner, where Docker is available, so the Testcontainers test runs there. The workflow also starts a Postgres 18 service and sets `DB_*` for the default datasource settings. The Testcontainers test ignores that service.

## Frontend (`frontend/`)

```bash
cd frontend && npm ci && npm run lint && npm run build
```

## Secret scanning (gitleaks)

**CI:** workflow job downloads the [MIT-licensed gitleaks CLI](https://github.com/gitleaks/gitleaks) at a pinned version (not `gitleaks-action`, which carries a separate commercial license — fine for many orgs; we avoid it for this personal repo).

**Local (optional):**

```bash
# Docker (no install)
docker run --rm -v "$(pwd):/repo" -w /repo \
  ghcr.io/gitleaks/gitleaks:v8.24.2 detect --source /repo --verbose --redact
```

Or install the release binary from GitHub and run `gitleaks detect --source .` from the repo root.

**This workspace:** `gitleaks` is not on PATH; use Docker or a release tarball.

## Not yet

- Error Prone, Spec Kit, OpenSpec, pre-commit hooks
