# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NorthlakeBridgeVentures** is a Spring Boot 4.0.3 REST API backed by PostgreSQL. It uses Spring Data JPA for persistence and Spring Web MVC for HTTP endpoints. Java 17 is required.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=NorthlakeBridgeVenturesApplicationTests

# Run a single test method
./mvnw test -Dtest=NorthlakeBridgeVenturesApplicationTests#contextLoads
```

## Configuration

- Main config: `src/main/resources/application.properties`
- Active database: PostgreSQL (`localhost:5432/mydb`); dialect is `PostgreSQLDialect`
- Both `postgresql` and `mysql-connector-j` drivers are on the classpath — only PostgreSQL is configured
- H2 is on the classpath (`runtime`) and can be used for in-memory test profiles
- `spring.jpa.hibernate.ddl-auto=update` — Hibernate manages schema automatically
- `spring.jpa.show-sql=true` — SQL is logged to console
- Note: `application.properties` is missing `#` prefixes on the HikariCP and JPA comment lines (lines 17, 23); treat those as comments

## Architecture

- **Entry point**: `NorthlakeBridgeVenturesApplication.java`
- **Package root**: `com.example.demo`

Current layers and packages:

| Package | Contents |
|---|---|
| `com.example.demo.hello` | `HelloControllerClient` (`/api/hello`) + `HelloService` |
| `com.example.demo.entity` | `User` — JPA entity mapped to `users` table (`id`, `name`, `role`) |
| `com.example.demo.repository` | `UserRepository` — `JpaRepository<User, Long>` |
| `com.example.demo.sketch` | Scratch/experimental code; not production |

There is no controller or service for `User` yet — only the entity and repository exist.

New features should follow the Controller → Service → Repository → Entity layering, with each domain in its own subpackage (e.g., `com.example.demo.user`).

## Code Style (from `.claude/rules/java/`)

- Use **constructor injection**, never `@Autowired` field injection
- Use **records** for DTOs and value types; mark entity fields `final` where possible
- Return `Optional<T>` from repository finder methods; never call `.get()` without `.isPresent()`
- Domain exceptions should extend `RuntimeException`
- Keep stream pipelines to 3–4 operations; prefer loops for complex logic
- Use `@DisplayName` and `methodName_scenario_expectedBehavior` naming for tests
- Use **AssertJ** (`assertThat`) and **Mockito** for unit tests; **Testcontainers** for DB integration tests
