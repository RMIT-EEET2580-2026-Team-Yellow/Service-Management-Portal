# Fixinh — Service Management Portal (SMT)

Backend for the Repair Service Booking System's **Service Management** subsystem — Staff,
Booking, Technician, Notification and Statistics modules — built for EEET2580 Milestone work by
**Squad Yellow, Team SMT**.

This subsystem is one half of the overall Fixinh system. The other half, **Service Booking
Portal (SBT)**, is a separate repository/deployment that this app integrates with over HTTPS
(see [Running alongside SBT](#running-alongside-sbt-optional) below). SMT runs and is fully
usable on its own — SBT is only needed for the cross-team rating/statistics features.

## Tech stack

- **Backend:** Java 17, Spring Boot 4.1, MongoDB (sharded: a northern and a southern region
  database, per SRS 3.1.U1/U2)
- **Frontend:** React + Vite, Tailwind CSS
- **Auth:** JWT (RS256), plus optional Google Sign-In
- **Containerization:** Docker Compose (backend, frontend, MongoDB)

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (this is the only hard
  requirement — everything else runs inside containers)
- A modern browser (Chrome/Edge/Firefox)

You do **not** need Java, Maven, or Node installed locally — the Docker images build everything.

## Quick start

No setup step, no file to copy or edit. From the project root:

```bash
docker compose up --build
```

`docker-compose.yml` has real working default values baked directly into the `environment:`
blocks for both the backend and frontend services (Mongo connection strings, the Google OAuth
client ID) — that's all this app needs, and it's already there. First run takes a few minutes
(Maven dependency download + npm install). Wait until the `smt-backend` container reports healthy
in the logs before opening the frontend — the backend seeds the database (Master Admin, Staff,
Service Types, Bookings) on first boot, which the frontend depends on.

**Open the app:**
- Frontend: **https://localhost:5173**
- Backend API: **https://localhost:8080/api/v1**

Both serve HTTPS with a **self-signed certificate** (there's no real domain to get a real one
for). Your browser will show a privacy warning on first visit — click **Advanced → Proceed to
localhost (unsafe)** (wording varies by browser). This is expected and safe for local dev.

**Log in** with the seeded Master Admin account:
```
Email:    admin@fixinh.vn
Password: Admin@123
```

## Running on a different machine / IP

Everything above assumes the frontend, backend and browser are all on the same machine
(`localhost`). If you're running this on a remote host or VM and opening it from another machine,
the only thing you need to change is the **host/IP the frontend uses to reach the backend** —
`VITE_API_PROXY_TARGET` in `frontend/vite.config.js` (defaults to `https://backend:8080`, the
Docker-internal address) and, if accessing the frontend itself from another machine,
`smt.cors.allowed-origin` in `backend/src/main/resources/application.properties` (defaults to
`https://localhost:5173`). Nothing else in the configuration needs to change.

## Overriding a default (optional)

Everything runs from the baked-in defaults in `docker-compose.yml` — you don't need to do this.
If you ever do want to point at a different Mongo (e.g. a shared Atlas cluster) or swap the Google
OAuth client, create a `.env` file at the project root and/or `frontend/.env` (see
`.env.example` / `frontend/.env.example` for the full list of variables) — Docker Compose picks
these up automatically and they take priority over the hard-coded defaults. Restart
(`docker compose restart <service>`) after changing either file.

## Testing the API directly (Postman, curl, etc.)

The backend only accepts **HTTPS** on port 8080 — a plain `http://` request gets rejected with
`This combination of host and port requires TLS.` Always use `https://localhost:8080/...`.

Because the certificate is self-signed, HTTP clients will also complain about it:
- **Postman:** click **Disable SSL Verification** on the failed request, or turn it off globally
  under Settings → General → SSL certificate verification.
- **curl:** add `-k` / `--insecure`.

## Ports

| Service | Container port | Host port |
|---|---|---|
| Frontend (Vite dev server) | 5173 | 5173 |
| Backend (Spring Boot) | 8080 | 8080 |
| MongoDB | 27017 | 27018 |

## Running alongside SBT (optional)

Statistics and rating pages call out to SBT's API (`smt.integration.sbp.base-url`, defaulting to
`https://host.docker.internal:8443/api/v1`). If SBT isn't running, those specific pages/endpoints
will show connection errors — every other feature (Bookings, Staff, Technicians, Notifications,
login) works completely independently and is unaffected.

To exercise the full cross-team flow, also start the `ServiceBookingPortal` repo (its own
`docker compose up`) so something is actually listening on port 8443.

## Troubleshooting

- **"Google sign-in is not configured for this deployment"** — shouldn't happen out of the box
  anymore (the client ID is baked into `docker-compose.yml`), but if you've created your own
  `frontend/.env` to override it, double-check the variable name is exactly `VITE_GOOGLE_CLIENT_ID`
  (Vite only exposes env vars prefixed with `VITE_`) and restart the frontend container afterwards
  (`docker compose restart frontend`) — Vite only reads env at startup, a browser refresh alone
  won't pick up the change.
- **"No Fixinh account is linked to this Google email"** — this is expected, not a bug: Google
  sign-in only authenticates users who already have a Staff/Admin account with that exact email
  in the system (it never auto-creates an account). Use the seeded admin's email/password login,
  or create a Staff account with your own email via `POST /api/v1/staff` first.
- **Port already in use** — another process (often a previous `docker compose up` that wasn't
  cleanly stopped) is holding 5173/8080/27018. Run `docker compose down` first, or check
  `docker ps` for stale containers.
- **Browser/Postman SSL warnings** — expected, see [Quick start](#quick-start) and
  [Testing the API directly](#testing-the-api-directly-postman-curl-etc) above; the certificates
  are self-signed for local development, not a sign anything is broken.
