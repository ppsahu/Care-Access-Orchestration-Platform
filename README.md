# Care Access Orchestration Platform

**Find care. Book with confidence. Keep appointments in sync.**

A patient-friendly scheduling experience that brings provider search and appointment administration into one place. Search synthetic provider availability, reserve an appointment, review upcoming bookings, or cancel a time that no longer works.

> **Administrative support only:** this platform does not diagnose symptoms, recommend treatment, or make clinical decisions. All demo data is synthetic.

## What it can do

- **Find available care:** Search appointment options by specialty, ZIP code, and date range.
- **See the details up front:** Review the provider, clinic, location, and appointment time before booking.
- **Book an appointment:** Reserve an available time through the patient-facing React interface.
- **Manage appointments:** View upcoming bookings and cancel an appointment when plans change.
- **Prevent slot conflicts:** Use a database transaction and row lock so concurrent requests cannot claim the same appointment slot.
- **Try a realistic demo:** Explore seeded synthetic providers and appointment availability without real patient data.

## Project status

The current working slice includes the React + TypeScript patient UI, Java 21 / Spring Boot API, PostgreSQL migrations, synthetic seed data, provider availability search, booking, cancellation, and local demo patient identity.

Referral and coverage validation, rescheduling, waitlist offers, Kafka event delivery, Redis caching, Spring AI request interpretation, and production authentication are planned next. The demo `X-Demo-Patient` header is for local development only and is **not authentication**.

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
