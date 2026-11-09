# ADR 0002: Session cookie authentication with CSRF for the SPA

- **Status:** Accepted
- **Date:** 2026-11-09
- **Context:** Phase 1 adds login and protected APIs. The React SPA talks to the Spring Boot API from the browser. Kasper chose the auth shape on 2026-11-05; this ADR records it so agents and future contributors do not re-litigate JWT vs session.
- **Decision:**
  1. Use **server-side HTTP sessions** managed by **Spring Security** (session id stored in a cookie, session state on the server).
  2. Session cookie attributes: **HttpOnly** (JavaScript cannot read it), **Secure** (HTTPS only in production), **SameSite** (limits cross-site cookie sends; use an appropriate value such as `Lax` or `Strict` per deployment).
  3. **CSRF protection** with Spring’s **cookie-to-header** pattern: the server exposes a CSRF token (often via cookie and/or response header); the SPA sends it back on mutating requests (e.g. `POST`, `PUT`, `DELETE`) in a header Spring expects (e.g. `X-XSRF-TOKEN`).
  4. **Same-origin in practice:** in development, the Vite dev server **proxies `/api`** to the backend so browser requests stay same-origin; production should serve SPA and API under one site origin or an equivalent safe setup.
  5. **Out of scope for this decision:** JWT access tokens, refresh tokens, and storing auth tokens in **localStorage** or **sessionStorage**.
- **Consequences:**
  - **Pros:** Simple mental model for a first-party SPA; easy logout and session invalidation on the server; HttpOnly session cookie reduces XSS theft of the session id compared to JS-readable tokens.
  - **Cons:** Server must store or look up session state; horizontal scaling needs sticky sessions or shared session storage later; every state-changing call must include CSRF handling in tests and client code.
  - **Phase 1 must implement:** `SecurityConfig` (or equivalent) with session creation and authorization rules; CSRF enabled for browser clients; session cookie flags configured for dev/prod; frontend `fetch`/API client wiring to send credentials (`credentials: 'include'`) and the CSRF header on writes; keep using the Vite `/api` proxy locally.
  - **Testing:** MVC/integration tests need CSRF tokens (or test-only CSRF disable only where explicitly justified); tests that log in must use session cookies like a browser; document patterns in `docs/testing.md` when auth tests land.
- **Alternatives considered:**
  - **JWT in localStorage** — rejected: any XSS can steal the token; no server-side revoke without extra infrastructure.
  - **JWT in HttpOnly cookie** — rejected for phase 1: still needs CSRF on mutating requests; revocation and rotation are harder than ending a server session.
  - **OAuth2 / OpenID Connect** — deferred: can wrap external IdPs later if the product needs social or enterprise login; does not replace the need for a first-party session for the API unless we adopt a full OIDC SPA flow.

**Plain terms**

- **CSRF (cross-site request forgery):** A trick where another site makes the browser call your API with cookies the user already has, unless the server checks a secret token only your SPA knows how to send.
- **HttpOnly:** A cookie flag that stops page JavaScript from reading it, so a typical XSS script cannot copy your session id out of `document.cookie`.
- **SameSite:** A cookie flag that tells the browser when **not** to attach the cookie to requests that start from other websites, which cuts down cross-site cookie abuse.
