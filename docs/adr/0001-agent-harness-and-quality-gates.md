# ADR 0001: Agent harness and Maven-backed quality gates

- **Status:** Accepted
- **Date:** 2026-11-07
- **Context:** Phase 0 monorepo; agents and humans need a small, honest contract. Markdown alone does not prevent drift.
- **Decision:**
  1. Add root `AGENTS.md` as the single CLI-oriented entry point; deeper material lives under `docs/`.
  2. Bind **Spotless** (Google Java Format, pinned formatter version) and **ArchUnit** (minimal package-boundary rules) into `./mvnw verify`.
  3. Run **gitleaks** in CI using the MIT-licensed CLI release (pinned version), not the commercial-licensed GitHub Action wrapper.
  4. Keep CI commands aligned with `AGENTS.md` verify section.
  5. Separate **enforced** technical gates from **planned** invoicing invariants in `docs/invariants.md`.
- **Consequences:**
  - Every backend change must pass format and architecture tests before merge.
  - ArchUnit rules cover current package layout only; business invariants remain undocumented-by-code until phase 1+.
  - Agents must read docs explicitly; Cursor rules/hooks are out of scope and not auto-loaded.
- **Alternatives considered:**
  - `gitleaks-action` at a pinned SHA — rejected for personal repo license clarity.
  - Hexagonal modules / Modulith — rejected as out of scope for phase 0.
