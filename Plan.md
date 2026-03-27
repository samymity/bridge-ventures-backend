# Plan: Employee Table & CRUD REST API

## Overview
Add a fully functional `Employee` domain with a PostgreSQL-backed table and REST endpoints for Create, Read, Update, and Delete operations.

---

## Steps

### Step 1 — Create the JPA Entity (`Employee`) ✅
- **File**: `src/main/java/com/example/demo/employee/Employee.java`
- Annotate with `@Entity`, `@Table(name = "employees")`
- Fields:
  - `id` — `Long`, `@Id`, `@GeneratedValue(strategy = IDENTITY)`
  - `firstName` — `String`
  - `lastName` — `String`
  - `email` — `String` (unique)
  - `department` — `String`
  - `salary` — `BigDecimal`
- No-arg constructor required by JPA; add an all-args constructor for convenience
- Hibernate will auto-create/update the `employees` table (`ddl-auto=update`)

---

### Step 2 — Create the Spring Data Repository ✅
- **File**: `src/main/java/com/example/demo/employee/EmployeeRepository.java`
- Extend `JpaRepository<Employee, Long>`
- Add `Optional<Employee> findByEmail(String email)` for email-uniqueness lookups

---

### Step 3 — Create Request/Response DTOs ✅
- **File**: `src/main/java/com/example/demo/employee/EmployeeRequest.java` — record for create/update input
- **File**: `src/main/java/com/example/demo/employee/EmployeeResponse.java` — record for API output (excludes internal fields if any)
- Map between entity and DTO inside the service, not the controller

---

### Step 4 — Create a Custom Exception ✅
- **File**: `src/main/java/com/example/demo/employee/EmployeeNotFoundException.java`
- Extend `RuntimeException`; include the `id` in the message
- Used by service when a lookup by ID returns empty

---

### Step 5 — Create the Service Layer ✅
- **File**: `src/main/java/com/example/demo/employee/EmployeeService.java`
- Annotate with `@Service`
- Use **constructor injection** for `EmployeeRepository`
- Methods:
  - `List<EmployeeResponse> findAll()`
  - `EmployeeResponse findById(Long id)` — throws `EmployeeNotFoundException`
  - `EmployeeResponse create(EmployeeRequest request)`
  - `EmployeeResponse update(Long id, EmployeeRequest request)` — throws `EmployeeNotFoundException`
  - `void delete(Long id)` — throws `EmployeeNotFoundException`

---

### Step 6 — Create the REST Controller ✅
- **File**: `src/main/java/com/example/demo/employee/EmployeeController.java`
- Annotate with `@RestController`, `@RequestMapping("/api/employees")`
- Use **constructor injection** for `EmployeeService`
- Endpoints:

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| GET | `/api/employees` | List all employees | 200 |
| GET | `/api/employees/{id}` | Get one employee | 200 / 404 |
| POST | `/api/employees` | Create employee | 201 |
| PUT | `/api/employees/{id}` | Full update | 200 / 404 |
| DELETE | `/api/employees/{id}` | Delete employee | 204 / 404 |

---

### Step 7 — Add a Global Exception Handler ✅
- **File**: `src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`
- Annotate with `@RestControllerAdvice`
- Handle `EmployeeNotFoundException` → HTTP 404 with a JSON error body
- Handle generic `Exception` → HTTP 500 (no internal detail exposed to client)

---

### Step 8 — Write Unit Tests for the Service ✅
- **File**: `src/test/java/com/example/demo/employee/EmployeeServiceTest.java`
- Use `@ExtendWith(MockitoExtension.class)`, mock `EmployeeRepository`
- Test cases:
  - `findById` returns response when found
  - `findById` throws `EmployeeNotFoundException` when not found
  - `create` saves and returns response
  - `update` updates fields and returns response
  - `delete` calls repository delete

---

### Step 9 — Write Integration/Controller Tests ✅
- **File**: `src/test/java/com/example/demo/employee/EmployeeControllerTest.java`
- Use `@WebMvcTest(EmployeeController.class)`, mock `EmployeeService`
- Test each endpoint for happy path and 404 scenario
- Note: Spring Boot 4.x uses `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  and `tools.jackson.databind.ObjectMapper` (Jackson 3.x)

---

### Step 10 — Verify End-to-End ✅
- Build: `./mvnw clean package -DskipTests` — BUILD SUCCESS
- App started on `localhost:8080`; `employees` table auto-created by Hibernate

Smoke test results:

| Endpoint | Result |
|----------|--------|
| POST `/api/employees` | 201 — employee created with id=1 |
| GET `/api/employees` | 200 — returns list with the new employee |
| GET `/api/employees/1` | 200 — returns employee by id |
| PUT `/api/employees/1` | 200 — lastName updated to Smith, salary to 100000 |
| DELETE `/api/employees/1` | 204 — deleted |
| GET `/api/employees/1` (after delete) | 404 — `{"status":404,"message":"Employee not found: id=1"}` |

---

## File Summary

```
src/main/java/com/example/demo/
  employee/
    Employee.java               ← JPA entity
    EmployeeRepository.java     ← Spring Data repo
    EmployeeRequest.java        ← input DTO (record)
    EmployeeResponse.java       ← output DTO (record)
    EmployeeNotFoundException.java
    EmployeeService.java        ← business logic
    EmployeeController.java     ← REST endpoints
  exception/
    GlobalExceptionHandler.java ← @RestControllerAdvice

src/test/java/com/example/demo/
  employee/
    EmployeeServiceTest.java
    EmployeeControllerTest.java
```
