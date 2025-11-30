# ERP Sales Backend (Order Management System)

Simple, DDD-based backend for the Sales (Penjualan) module of an ERP system. Built with Kotlin, Ktor, Exposed and PostgreSQL, following OOP, Domain-Driven Design, and Layered Architecture to produce clean, testable, and maintainable code.

## Team & Contributions
- Agam Gunata (224443001) — Lead Domain & Analyst
    - Designed UML class diagrams and domain entity modelling.
    - Defined business rules (stock validation, order status transitions).
    - Identified Aggregate Root: `SalesOrder` and domain invariants.

- Muhammad Daffi Izzuddin (224443013) — Lead Implementation (OOP / API)
    - Implemented Entities, Services, and Repository interfaces in Kotlin.
    - Built REST API using Ktor routing and controllers.
    - Configured Docker setup and PostgreSQL integration.

- Azfa Hafshiam Dilaga (224443002) — Lead Testing & Documentation
    - Produced automated API docs with OpenAPI / Swagger UI.
    - Ensured code quality and end-to-end test coverage.

> Note: Each team member must keep their contributions section up to date in this `README.md`.

## Suggested Role Responsibilities
- Lead Domain & Analyst: domain modelling, class diagrams, business rule definition, acceptance criteria.
- Lead Implementation (OOP / API): translate design into idiomatic Kotlin, implement services/repositories/controllers, ensure clean architecture.
- Lead Testing & Documentation: define test scenarios, write and run unit/integration tests, maintain API docs and developer guides.

## Architecture Summary
- Layered Architecture:
    - Domain Layer: Entities (SalesOrder, Customer), Value Objects (Email, Money, SKU), Repository interfaces — contains pure business logic.
    - Application Layer: Services such as `SalesService`, `FulfillmentService` — orchestrates use cases.
    - Infrastructure Layer: Database (Exposed + PostgreSQL), Web API (Ktor), Docker configuration.

- DDD Decisions:
    - Aggregate Root: `SalesOrder` — Order lines manipulated only through the aggregate to preserve invariants (total price, status transitions).
    - Value Objects: `Email`, `Money`, `SKU` implement self-validation to avoid primitive obsession.

## Tech Stack
- Language: Kotlin (JDK 17+)
- Framework: Ktor (async web)
- ORM / SQL: Exposed
- Database: PostgreSQL (via Docker)
- API docs: OpenAPI / Swagger UI
- Build: Gradle (wrapper included)

## Getting Started (Windows)
Prerequisites: Java JDK 17\+, Docker Desktop (running), IntelliJ IDEA (recommended).

1. Start database:
    - Open project root in PowerShell / CMD:
        - `docker-compose up -d`
    - If connection errors occur:
        - `docker-compose down -v`
        - `docker-compose up -d`
    - Ensure container `erp_sales_db` is `Running`.

2. Run server:
    - From IntelliJ: Run `Application.kt`
    - From terminal (Windows):
        - `gradlew run`
    - Wait for logs indicating seeding and server start (default port shown in logs).

3. Access:
    - API Docs (Swagger UI): `http://localhost:8081/swagger`

4. Tests:
    - Run unit tests:
        - `./gradlew test` (or `gradlew test` on Windows)

## Key Features
- Create Order, Add Item, Confirm Order, Fulfillment (Ship) with tracking ID.
- Business rules enforced in domain: non-empty confirmed orders, stock validation, automatic total price calculation.
- Unit tests covering email validation, order total calculation, and prevention of confirming empty orders.

## Notes for Contributors
- Keep `README.md` updated with specific contributions and any changes to run instructions.
- Follow DDD boundaries: modify entities and invariants only after design discussion with Lead Domain & Analyst.
- Use unit tests to validate all business rules before merging changes.