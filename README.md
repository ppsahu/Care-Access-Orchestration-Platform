# Care Access Orchestration Platform

Synthetic-data portfolio project for provider search and appointment administration. It does not diagnose conditions or recommend treatment.

Scaling assumptions, capacity objectives, architecture, bottlenecks, and load-test plan: [Scaling plan](docs/SCALING_PLAN.md).

## Current implementation

- Java 21 / Spring Boot REST API
- PostgreSQL schema and deterministic synthetic slots
- Provider availability search
- Patient booking, cancellation, and rescheduling endpoints
- Database row locking and an active-slot uniqueness constraint for booking races
- Atomic idempotency-key claims for booking and rescheduling retries; concurrent identical retries share one result, and keys cannot be reused with a different payload
- Bounded synthetic demo data, PostgreSQL indexes, Flyway migrations, and Actuator health/metrics

The patient UI includes a demo sign-in backed by a server session. Booking and appointment management use the authenticated session rather than a patient ID supplied by the browser. This is demo authentication only; production authentication, Kafka outbox delivery, Redis availability caching, referral/coverage integrations, waitlist offers, and Spring AI request interpretation remain future work.

## Run

```sh
docker compose up -d postgres
cd backend
mvn spring-boot:run
```

In another terminal, start the patient UI:

```sh
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The synthetic demo credentials are `demo@careway.test` / `careway-demo`.

Search:

```sh
curl 'http://localhost:8080/api/v1/providers?specialty=CARDIOLOGY&postalCode=02139&from=2026-12-01T00:00:00-05:00&to=2026-12-31T23:59:59-05:00'
```

Book a returned slot:

```sh
curl -X POST http://localhost:8080/api/v1/bookings \
  -H 'Content-Type: application/json' -H 'Idempotency-Key: demo-booking-001' \
  -b careway.cookies \
  -d '{"slotId":"a1000000-0000-4000-8000-000000000001"}'
```

Sign in first and save the session cookie for the booking example:

```sh
curl -c careway.cookies -H 'Content-Type: application/json' \
  -d '{"username":"demo@careway.test","password":"careway-demo"}' \
  http://localhost:8080/api/v1/auth/login
```

The seed data uses December 2026 appointment times. No real patient or provider data is included.

## API

- `GET /api/v1/providers`: `specialty`, `postalCode`, `from`, `to`, optional `page` and `size` (max 50; max date window 90 days)
- `POST /api/v1/auth/login`: demo username/password; establishes an HttpOnly, SameSite=Lax session cookie
- `GET /api/v1/auth/me`: returns the signed-in synthetic patient
- `POST /api/v1/auth/logout`: invalidates the session
- `POST /api/v1/bookings`: signed-in session, `Idempotency-Key`, and `{ "slotId": "..." }`
- `GET /api/v1/bookings`: signed-in session
- `POST /api/v1/bookings/{id}/cancel`: signed-in session
- `POST /api/v1/bookings/{id}/reschedule`: signed-in session, `Idempotency-Key`, and `{ "slotId": "..." }`
- `GET /actuator/health`

Architecture and full delivery plan: [../CARE_ACCESS_PLATFORM_DESIGN.md](../CARE_ACCESS_PLATFORM_DESIGN.md).
