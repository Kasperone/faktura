# Testing and quality gates

## Backend (`backend/`)

| Gate | Command | Phase |
|------|---------|-------|
| Unit / MVC tests | `./mvnw test` | `test` |
| Spotless | `./mvnw spotless:check` | `verify` |
| ArchUnit | `./mvnw test` (`ArchitectureRulesTest`) | `test` |
| Package + Spring Boot repackage | `./mvnw verify` | `verify` |

Fix formatting:

```bash
cd backend && ./mvnw spotless:apply
```

CI sets `DB_*` env vars and runs `./mvnw -B verify` with a Postgres 18 service container.

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

## Not in phase 0

- Testcontainers (phase 1)
- Error Prone, Spec Kit, OpenSpec, pre-commit hooks
