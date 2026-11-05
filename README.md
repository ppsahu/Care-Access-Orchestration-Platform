# Care Access Orchestration Platform

Synthetic-data portfolio project for provider search and appointment administration. It helps with administrative navigation only; it does not diagnose conditions or recommend treatment.

## Current implementation

- Java 21 / Spring Boot API with PostgreSQL migrations and synthetic slots
- Provider availability search and booking/cancellation lifecycle
- Database row lock for concurrent slot claims
- React + TypeScript patient UI for search, booking, and upcoming appointments
- Synthetic demo patient identity; the `X-Demo-Patient` header is local-only and is not authentication

Kafka, Redis, referral and coverage adapters, waitlist, and Spring AI request parsing remain planned backend slices.

## UI Page
<img width="1697" height="1301" alt="image" src="https://github.com/user-attachments/assets/63d958ad-1d37-4b57-8354-a87e5210da25" />


## Run locally

Start PostgreSQL from the project root:

```sh
docker compose up -d postgres
```

In one terminal:

```sh
cd backend
mvn spring-boot:run
```

In another terminal:

```sh
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal (usually http://localhost:5173). Vite proxies API requests to the backend at localhost:8080.

Search demo:

```sh
curl 'http://localhost:8080/api/v1/providers?specialty=CARDIOLOGY&postalCode=02139&from=2026-12-01T00:00:00Z&to=2026-12-31T23:59:59Z'
```

All seed appointments use December 2026 dates. All data is synthetic.

## API

- `GET /api/v1/providers?specialty=&postalCode=&from=&to=`
- `POST /api/v1/bookings` with `X-Demo-Patient` and `{ "slotId": "..." }`
- `GET /api/v1/bookings` with `X-Demo-Patient`
- `POST /api/v1/bookings/{id}/cancel` with `X-Demo-Patient`
- `GET /actuator/health`

No performance claims are included; collect measurements before reporting them.
