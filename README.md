# Backend Assignment - News Management System

This project is a backend implementation of a **News Management System**, developed as part of the AppsWave Backend Developer assignment. It includes APIs for authentication, user management, and news management with **role-based access control**, built using **Spring Boot 2.7.14**, **Java 11**, **Tomcat 9**, and **MySQL**.

---

## Table of Contents

1. [Technologies Used](#technologies-used)
2. [Security Implementation](#security-implementation)
3. [Workflow Engine](#workflow-engine)
4. [Scheduler for Soft Deletion](#scheduler-for-soft-deletion)
5. [Database and ORM](#database-and-orm)
6. [Validation](#validation)
7. [API Documentation](#api-documentation)
8. [Project Structure](#project-structure)
9. [Role-Based Access Control](#role-based-access-control)
10. [Exception Handling](#exception-handling)
11. [Setup Instructions](#setup-instructions)
12. [API Endpoints](#api-endpoints)
13. [Deliverables](#deliverables)

---

## Technologies Used

- **Framework**: Spring Boot 2.7.14
- **Java Version**: JDK 11
- **Web Server**: Tomcat 9
- **Database**: MySQL
- **ORM**: Hibernate
- **Security**: Spring Security with JWT (JSON Web Tokens)
- **Password Hashing**: BCryptPasswordEncoder
- **Workflow Engine**: Spring StateMachine
- **Scheduling**: Spring `@EnableScheduling`
- **Validation**: Java Bean Validation API (`javax.validation.constraints.*`)
- **API Documentation**: Swagger
- **Build Tool**: Maven

---

## Security Implementation

### JWT Authentication

- **Token Time-To-Live (TTL)**: 1 minute.
- Includes a **refresh token mechanism**.
- **Signature Algorithm**: HS512.
- Only specific endpoints are permitted without JWT; all others require authentication.
- Global method security is enabled using `@EnableGlobalMethodSecurity(prePostEnabled = true)` for **role-based authorization**.

### Password Hashing

- Passwords are hashed using `BCryptPasswordEncoder` before being stored in the database, ensuring secure authentication.

---

## Workflow Engine

This project uses **Spring StateMachine** to manage transitions between news statuses.

- **Statuses**: `PENDING`, `APPROVED`, `DELETED`, and `PENDING_DELETION`.
- **Event Listeners**: A `StateMachineListener` is implemented to log status transitions.
- **WorkflowLog Entity**: Logs all actions (e.g., status changes) with timestamps, stored for auditing purposes.

---

## Scheduler for Soft Deletion

- A **Spring Scheduler** runs daily at midnight to softly delete news items whose publish dates have passed.
- **Cron Expression**: `0 0 0 * * ?`

---

## Database and ORM

- **Database**: MySQL
- **ORM Framework**: Hibernate
- Entities are mapped to database tables using **JPA annotations**.

---

## Validation

- **Input Validation**: 
  - Uses the Java Bean Validation API (`javax.validation.constraints.*`).
  - Ensures consistency and integrity of API request data.
  - Validates fields like `email`, `password`, and `name` against specific criteria.

---

## API Documentation

Swagger is used to document the APIs. You can access the Swagger UI here:

- **URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Project Structure

- **Controller Layer**: Contains API endpoints.
- **Service Layer**: Handles business logic.
- **Repository Layer**: DAO layer using Hibernate for database interactions.
- **Entities**: Represent database tables.
- **Enums**: Define roles and statuses.
- **Exceptions**: Custom exceptions and global exception handling.

---

## Role-Based Access Control

### Roles:

1. **USER**:
   - Access authentication APIs.
   - Read news.

2. **WRITER**:
   - Perform CRUD operations on news (publishing requires admin approval).

3. **ADMIN**:
   - Full access to all APIs.

### Default Role:

- If no role is specified during registration, the default role is `USER`.

---

## Exception Handling

- **Global Exception Handler**:
  - Uses `@RestControllerAdvice` to handle exceptions globally.
  - Custom exceptions and validation errors are handled gracefully.
  - Validation errors return detailed error messages.

---

## Setup Instructions

### Clone the Repository:

```bash
git clone <repository-url>
cd <project-folder>
