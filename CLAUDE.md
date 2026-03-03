# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NorthlakeBridgeVentures** is a Spring Boot 4.0.3 REST API backed by MySQL (AWS RDS). It uses Spring Data JPA for persistence and Spring Web MVC for HTTP endpoints. Java 17 is required.

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
- Secrets (gitignored): `application-secrets.properties` or `.env`
- DB password is injected via environment variable: `DB_PASSWORD`
- MySQL datasource points to AWS RDS; update the `spring.datasource.url` when targeting a different instance
- `spring.jpa.hibernate.ddl-auto=update` — Hibernate auto-manages schema; change to `validate` or `none` in production

## Architecture

- **Entry point**: `src/main/java/com/example/demo/NorthlakeBridgeVenturesApplication.java`
- **Package root**: `com.example.demo` (generated default; new code should live here or a subpackage)
- **Layers to build out**: Controller → Service → Repository (Spring Data JPA) → Entity (JPA `@Entity`)
- **Database**: MySQL via `mysql-connector-j`; Hibernate dialect `MySQLDialect`

## Key Conventions

- Place JPA entities, repositories, services, and controllers in subpackages under `com.example.demo` (e.g., `com.example.demo.product`, `com.example.demo.user`)
- Profile-specific properties use the `application-{profile}.properties` naming convention; `application-secret.properties` is gitignored for local secrets
- The Maven wrapper (`mvnw`) is the standard way to build — no local Maven installation required
