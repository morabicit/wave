# News Management System

This project is a backend implementation of a **News Management System**, developed as part of an assignment. It includes APIs for authentication, user management, and news management with **role-based access control**, built using **Spring Boot 2.7.14**, **Java 11**, **Tomcat 9**, and **MySQL**.

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
- **Scheduling**: Spring Task Scheduling
- **Validation**: Java Bean Validation API
- **API Documentation**: Swagger
- **Build Tool**: Maven

---

## Security Implementation

### JWT Authentication

- Implements JWT for securing APIs.
- **Token Time-To-Live (TTL)**: 1 minute.
- Includes a **refresh token mechanism** valid for 30 minutes.
- **Signature Algorithm**: HS512.
- Only specific endpoints are permitted without authentication;
  signup and login.
-  all others require a valid JWT.
- Role-based access control is enabled using Spring Security to secure methods and APIs.

### Password Hashing

- Passwords are hashed using the **BCryptPasswordEncoder** algorithm before being stored in the database, ensuring secure authentication and storage.
- 
# Authentication API

## Endpoints

### 1. POST `/api/auth/signup`
- Registers a new user.

### 2. POST `/api/auth/login`
- Logs in a user and returns an access token and refresh token.

### 3. POST `/api/auth/logout`
- Logs out a user.  
- Requires the access token in the Authorization header.

### 4. POST `/api/auth/refresh`
- Refreshes the access token for an extra 1 minute and return it for the user.  
- Requires the refresh token in the Authorization header (valid for 30 minutes).

---

## Workflow Engine

- Uses **Spring StateMachine** to handle state transitions for news statuses.
- **Statuses**: `PENDING`, `APPROVED`, `DELETED`, and `PENDING_DELETION`.
- Logs state transitions to the database via a custom WorkflowLog entity, ensuring traceability.
- **Listeners**: StateMachine listeners are implemented to monitor events and record logs.

---

## Scheduler for Soft Deletion

- Implements task scheduling using **Spring Task Scheduling**.
- The scheduler runs a daily task at midnight to softly delete expired news items based on their publish date.
- **Cron Expression**: `0 0 0 * * ?`

---

## Database and ORM

- **Database**: MySQL is used to store all entities and data.
- **ORM Framework**: Hibernate is used for interacting with the database.
- Entities are mapped to database tables using **Java Persistence API (JPA)** annotations.

---

## Validation

- Input validation is implemented using the **Java Bean Validation API**.
- Ensures data integrity by validating required fields, data types, and constraints.
- Examples of validations:
  - Validating email format.
  - Ensuring password meets security requirements.
  - Checking for non-null fields like `name` or `role`.

---

## API Documentation

- Swagger is used for API documentation.
- API endpoints are documented and can be tested using Swagger UI.
- **Access URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Project Structure

- **Controller Layer**: Handles API endpoints for authentication, user management, and news operations.
- **Service Layer**: Contains business logic for user and news workflows.
- **Repository Layer**: Manages database access using Hibernate and JPA.
- **Entities**: Represent database tables and include mappings for relationships.
- **Workflow Engine**: Manages status transitions for news using Spring StateMachine.
- **Enums**: Define roles and news statuses.
- **Exception Handling**: Includes a global exception handling mechanism.

---

## Role-Based Access Control

### Roles:

1. **USER**:
   - Can access authentication APIs and view news.
2. **WRITER**:
   - Can create, update, or delete news (requires admin approval for publishing).
3. **ADMIN**:
   - Has full access to all APIs and functionalities.

### Default Role:

- If no role is specified during registration, the default role is `USER`.

---

## Exception Handling

- Implements a global exception handling mechanism using **Spring Rest Controller Advice**.
- Custom exceptions are handled to provide meaningful error messages.
- Validation errors are processed and return detailed messages with field-specific issues.

---

## Setup Instructions

### Clone the Repository:

```bash
git clone <repository-url>
cd <project-folder>
