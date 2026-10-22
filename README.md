
# Transportation & Seat Reservation System API

A robust, production-grade Spring Boot backend application built for managing a transportation fleet (buses and shuttles), dynamic seat allocation, and customer travel bookings. 

---

## Key Features

* **Automated Data Seeding:** Safely pre-fills the database on startup with randomized fleets (license plates, vehicle capacities, types) and seat pools (window, middle, aisle) using an idempotent `CommandLineRunner`.
* **Transactional Booking Engine:** Ensures safe concurrency and data integrity when users book seats, preventing double-booking conflicts.
* **Custom Thread Pool Configuration:** Optimized execution thread pool (`requestExecutor`) tuned for high-concurrency request handling.
* **Global Exception Handling:** Centralized REST error handling providing clean, standardized error payloads and HTTP status codes (`404`, `409`, `500`).
* **Interactive API Documentation:** Fully documented with **Springdoc OpenAPI (Swagger UI)**.
* **Containerized Deployment:** Multi-stage `Dockerfile` optimized for lightweight, secure production container deployments.

---

##  Technology Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3 (Spring Web, Spring Data JPA, Spring Validation, Spring Scheduling)
* **Database:** Relational Database (H2 / PostgreSQL / MySQL compatible via JPA/Hibernate)
* **Documentation:** OpenAPI 3 / Swagger UI
* **Containerization:** Docker (Eclipse Temurin JDK/JRE 21)
* **Utilities:** Lombok, ThreadLocalRandom

---

##  Project Structure

```text
com.daniel
│
├── config/              # Thread pools & OpenAPI / Swagger configurations
├── controller/          # REST controllers (BookingController, GlobalExceptionHandler)
├── dto/                 # Request & Response records/DTOs
├── entity/              # JPA Entities (BookingEntity, CarEntity, SeatEntity)
├── exception/           # Custom domain exceptions
├── repository/          # Spring Data JPA repositories
├── service/             # Business logic layer (CustomerService)
└── util/                # Data generation & helper utilities (TransportUtil)

```

---

##  Getting Started Locally

### Prerequisites

* Java 21 installed
* Maven installed (or use the included `./mvnw wrapper`)

### 1. Clone the Repository

```bash
git clone https://github.com/daniel-oyoo/transportation-system.git
cd transportation-system

```

### 2. Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run

```

The application will start on port `8080` and automatically seed initial cars and seats if the database is empty.

---

##  Running with Docker

### 1. Build the Docker Image

```bash
docker build -t transportation-api:latest .

```

### 2. Run the Container

```bash
docker run -d \
  --name transportation-app \
  -p 8080:8080 \
  transportation-api:latest

```

---

##  API Documentation & Swagger UI

Once the application is running, you can explore and test all available endpoints interactively via Swagger UI:

* **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI JSON Docs:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Core Endpoints

* POST `/api/v1/booking` — Book a travel seat.
* GET `/api/v1/booking/fill` — Manually trigger transport data seeding.

```

```