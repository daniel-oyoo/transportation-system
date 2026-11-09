
# Easy Coach Kenya Demo Booking App

A passenger-booking demo built with a Spring Boot REST API and a React/Vite web client. It demonstrates trip search, seat selection, reservations, and booking-reference lookup. The Easy Coach Kenya name is used only to identify the sample operator concept; this project is not affiliated with or endorsed by Easy Coach.

**Demo data only:** the included routes, buses, departure times, and fares are illustrative. Verify all real operator details and obtain permission before presenting this as an official service.

## What works

- Search scheduled trips by origin, destination, and date.
- Show per-trip seat availability and reserve a seat.
- Compute the fare from the server-side trip record, not from the browser request.
- Prevent duplicate seat reservations with a database uniqueness constraint.
- Return and look up a UUID booking reference.
- Seed sample buses, seats, and trips only in the `dev` profile.
- Run separate `dev`, `test`, and `prod` application profiles.

Reservations are confirmed as **pay at station**. There is no card or M-Pesa integration.

## Run locally

Prerequisites: Java 21, Maven 3.9+, Node.js 20.19+ or 22.12+.

### Backend

From the repository root:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The default profile is `dev`; the local database is a persistent H2 file under `data/`. The demo records are inserted once when the trip table is empty. API docs are available at `http://localhost:8080/swagger-ui.html`.

### React web app

In another terminal:

```powershell
cd web
npm install
npm run dev
```

Open the Vite URL printed in the terminal (normally `http://localhost:5173`). The frontend reads the API base URL from `web/.env.local`; copy `web/.env.example` there to override it.

## Environment profiles

| Profile | Database | Schema | Intended use |
|---|---|---|---|
| `dev` | Persistent local H2 | Hibernate `update` | Local development with illustrative sample data |
| `test` | In-memory H2 | Hibernate `create-drop` | Automated tests; no sample data |
| `prod` | PostgreSQL | Flyway migrations; Hibernate `validate` | Deployment with externally supplied secrets |

Run tests with:

```powershell
mvn test
```

For production, set `SPRING_PROFILES_ACTIVE=prod` and supply `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `APP_CORS_ALLOWED_ORIGINS` through the deployment platform's secret/configuration system. The app expects the PostgreSQL JDBC URL form, for example `jdbc:postgresql://db-host:5432/easycoach`. Do not deploy the example credentials. The initial production schema is in `src/main/resources/db/migration`.

The backend container can be built with `docker build -t easy-coach-booking-api .`. It serves the API only; deploy the Vite build separately as a static site and set the production CORS origin to that site.

## API

- `GET /api/v1/trips?from=Nairobi&to=Kisumu&date=YYYY-MM-DD` — search scheduled trips.
- `GET /api/v1/trips/{tripId}/seats` — list seats and availability for a trip.
- `POST /api/v1/bookings` — reserve a seat. Body: `{"tripId":1,"seatId":1,"passengerName":"Amina Otieno","passengerPhone":"0712345678"}`.
- `GET /api/v1/bookings/{reference}` — look up a reservation.

The booking API does not accept a client-supplied fare. The fare and route are read from the selected trip.

## Before a real operator launch

This is a demo/MVP, not a complete commercial ticketing platform. Before handling real customers or payments, it still needs operator/staff authentication and authorization, audited trip/fleet management, verified schedules and fares, cancellation/refund rules, M-Pesa/payment-provider integration, customer notifications, deployment monitoring/backups, and a privacy/data-retention policy. The public booking lookup currently relies on possession of an unguessable booking reference.