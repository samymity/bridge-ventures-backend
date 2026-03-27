# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NorthlakeBridgeVentures** is a Spring Boot 4.0.3 REST API backed by PostgreSQL. It uses Spring Data JPA for persistence and Spring Web MVC for HTTP endpoints. Java 17 is required.

- GroupId: `com.northlake` | ArtifactId: `bridge-ventures`
- Root package: `com.example.demo`

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=EmployeeServiceTest

# Run a single test method
./mvnw test -Dtest=EmployeeServiceTest#findById_exists_returnsResponse
```

## Configuration

- Main config: `src/main/resources/application.properties`
- Active database: PostgreSQL (`localhost:5432/mydb`); credentials in `application.properties` (`username=admin`)
- `spring.datasource.password` has a leading space before the value — ensure it matches your actual PostgreSQL password exactly
- Both `postgresql` and `mysql-connector-j` drivers are on the classpath — only PostgreSQL is configured
- H2 is on the classpath (`runtime`) and can be used for in-memory test profiles
- `spring.jpa.hibernate.ddl-auto=update` — Hibernate manages schema automatically
- `spring.jpa.show-sql=true` — SQL is logged to console

## Architecture

The codebase has two production-grade modules and two incomplete/demo modules:

### Employee module — `com.example.demo.employee` (complete CRUD)

| Class | Role |
|---|---|
| `Employee` | `@Entity` mapped to `employees` table; unique email, salary as `BigDecimal(15,2)` |
| `EmployeeRepository` | `JpaRepository<Employee, Long>` + `findByEmail(String)` |
| `EmployeeService` | Business logic; maps entities ↔ DTOs via streams; throws `EmployeeNotFoundException` |
| `EmployeeController` | `@RestController` at `/api/employees`; returns 201 on POST, 204 on DELETE |
| `EmployeeRequest` | Record DTO for input (firstName, lastName, email, department, salary) |
| `EmployeeResponse` | Record DTO for output; static `from(Employee)` factory |
| `EmployeeNotFoundException` | Extends `RuntimeException`; caught by `GlobalExceptionHandler` |

REST surface: `GET /api/employees`, `GET /api/employees/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`

### Exception handling — `com.example.demo.exception`

`GlobalExceptionHandler` (`@RestControllerAdvice`) maps `EmployeeNotFoundException` → 404 and all other exceptions → 500. Uses inner `ErrorResponse(int status, String message)` record.

### Incomplete modules

- **`com.example.demo.entity` / `com.example.demo.repository`** — `User` entity + `UserRepository` exist but have no service or controller yet.
- **`com.example.demo.hello`** — Demo `HelloControllerClient` (`/api/hello/start`, `/api/hello/bye`) + `HelloService`. Uses `@Autowired` field injection (inconsistent with the rest of the codebase — use constructor injection for any new code here).
- **`com.example.demo.sketch`** — Empty placeholder; not production code.

### New features

Follow the pattern established in the Employee module: Controller → Service → Repository → Entity, each domain in its own subpackage (e.g., `com.example.demo.user`).

## Testing

| Class | Type | Framework |
|---|---|---|
| `NorthlakeBridgeVenturesApplicationTests` | Context load | `@SpringBootTest` |
| `EmployeeServiceTest` | Unit | `@ExtendWith(MockitoExtension.class)`, Mockito, AssertJ |
| `EmployeeControllerTest` | Web layer | `@WebMvcTest` + `MockMvc`, `@MockitoBean`, `jsonPath` assertions |

Use `@DisplayName` and `methodName_scenario_expectedBehavior` naming. Use **AssertJ** (`assertThat`, `assertThatThrownBy`) — not JUnit `assertEquals`.

## Code Style

- **Constructor injection** only — never `@Autowired` field injection
- **Records** for DTOs and value types; mark entity fields `final` where possible
- Return `Optional<T>` from finders; never call `.get()` without `.isPresent()` / use `.orElseThrow()`
- Domain exceptions extend `RuntimeException`
- Stream pipelines capped at 3–4 operations; prefer loops for complex logic
