# Care Access Orchestration Platform

Synthetic-data portfolio project for provider search and appointment administration. It does not diagnose conditions or recommend treatment.

Scaling assumptions, capacity objectives, architecture, bottlenecks, and load-test plan: [Scaling plan](docs/SCALING_PLAN.md).

## Current implementation

- Java 21 / Spring Boot REST API
- PostgreSQL schema and deterministic synthetic slots
- Provider availability search
- Patient booking, cancellation, and rescheduling endpoints
- Database row locking and an active-slot uniqueness constraint for booking races
- Idempotency keys for booking and rescheduling retries; a key cannot be reused with a different payload
- Bounded synthetic demo data, PostgreSQL indexes, Flyway migrations, and Actuator health/metrics

Kafka outbox delivery, Redis availability caching, deterministic referral/coverage adapters, waitlist offers, Spring AI request interpretation, and production authentication are planned next. The demo patient header is for local development only and is not authentication.

## Run

```sh
docker compose up -d postgres
cd backend
mvn spring-boot:run
```

Search:

```sh
curl 'http://localhost:8080/api/v1/providers?specialty=CARDIOLOGY&postalCode=02139&from=2026-12-01T00:00:00-05:00&to=2026-12-31T23:59:59-05:00'
```

Book a returned slot:

```sh
curl -X POST http://localhost:8080/api/v1/bookings \
  -H 'Content-Type: application/json' -H 'X-Demo-Patient: patient-demo-001' \
  -d '{"slotId":"a1000000-0000-4000-8000-000000000001"}'
```

The seed data uses December 2026 appointment times. No real patient or provider data is included.

## API

- `GET /api/v1/providers`: `specialty`, `postalCode`, `from`, `to`
- `POST /api/v1/bookings`: `X-Demo-Patient`, `Idempotency-Key`, and `{ "slotId": "..." }`
- `GET /api/v1/bookings`: `X-Demo-Patient`
- `POST /api/v1/bookings/{id}/cancel`: `X-Demo-Patient`
- `POST /api/v1/bookings/{id}/reschedule`: `X-Demo-Patient`, `Idempotency-Key`, and `{ "slotId": "..." }`
- `GET /actuator/health`

Architecture and full delivery plan: [../CARE_ACCESS_PLATFORM_DESIGN.md](../CARE_ACCESS_PLATFORM_DESIGN.md).
