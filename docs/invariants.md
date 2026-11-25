# Invariants

**Enforced** means a failing gate blocks `mvn verify` or CI. **Planned** is design intent only — no implementation yet.

## Enforced (technical)

| Invariant | Gate |
|-----------|------|
| Java sources match Google Java Format | Spotless (`spotless:check` on `verify`) |
| `config` does not depend on `customer` / `invoice` / `organization` / `ksef` | ArchUnit |
| Each feature package does not depend on the other feature packages | ArchUnit |
| `shared` does not depend on feature packages | ArchUnit |
| Repository scan finds no known secret patterns | gitleaks (CI) |
| Schema changes ship as Flyway versioned migrations; Hibernate does not generate schema (`spring.jpa.hibernate.ddl-auto=validate`) | `PostgresFlywayContextTest` (Testcontainers PostgreSQL 18) |
| A request does not keep a JPA persistence context open after the transaction (`spring.jpa.open-in-view=false`) | `PostgresFlywayContextTest` |

## Planned (invoicing / domain — not enforced)

These are product rules for later phases; **do not assume they exist in code**:

- Unique invoice numbering per organization / legal period (Polish practice).
- Invoice totals = sum of line items; tax rounding policy defined and tested.
- Issuer and buyer identity fields required before an invoice is **issued** (not draft).
- Immutable issued invoice (corrections via corrective documents, not silent edits).
- KSeF submission state machine and audit trail for government reporting.
- PDF / UBL output consistent with stored invoice data.

## Doc vs code

If this file claims enforcement that ArchUnit or tests do not implement, treat that as a documentation bug. Current ArchUnit scope is **package boundaries only** — not business invariants.
