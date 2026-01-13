# Tuum Core Banking Test Assignment

## Overview

This project provides:

- Account management with balances and transaction history
- Publishing account and transaction events to RabbitMQ

The goal of this assignment is to demonstrate knowledge of **Java 17+, Spring Boot, MyBatis, PostgreSQL, RabbitMQ, Gradle, and JUnit**

---

## Technologies

- **Java 17+**
- **Spring Boot**
- **MyBatis**
- **Gradle**
- **PostgreSQL**
- **RabbitMQ**
- **JUnit 5**
- **Testcontainers**

---

## Running the Application

### 1. Build the project

```
./gradlew clean build
```

### 2. Run using Docker Compose

```
docker-compose up --build
```

This will start:

- PostgreSQL database
- RabbitMQ broker
- Banking application (Spring Boot)

The application will be accessible at `http://localhost:8080`.

---

## REST API Endpoints

### 1. Create Account

**POST** `/accounts`

**Request JSON:**

```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "country": "EE",
  "currencies": ["EUR", "USD"]
}
```

**Errors:**

  "message": "Invalid currency",

---

### 2. Get Account

**GET** `/accounts/{accountId}`

**Errors:**

  "message": "Account not found",

---

### 3. Create Transaction

**POST** `/accounts/{accountId}/transactions`

**Request JSON:**

```json
{
  "amount": 50,
  "currency": "EUR",
  "direction": "IN",
  "description": "Deposit"
}
```

**Errors:**

  "message": "Invalid currency",

  "message": "Insufficient funds",

---

### 4. Get Transactions

**GET** `/accounts/{accountId}/transactions?limit=10&offset=0`

**Errors:**

  "message": "Account not found",

---

## Testing

- Integration tests cover account and transaction flows.
- Test coverage is **> 80%**.
- Testcontainers used for PostgreSQL and RabbitMQ.

Run tests:

```
./gradlew test
```

---

## Design Choices

- **TransactionalPublisherService** ensures events are only published after DB commit.
- **UUIDTypeHandler** used for mapping UUIDs in PostgreSQL.
- **Event-driven architecture** allows decoupled consumers via RabbitMQ.
- **MyBatis** chosen for simplicity in mapping SQL to objects.

---

## Performance

- ~500-1000 transactions per second.
- Performance depends on DB size, RabbitMQ throughput, and network latency.

---

## Scaling Considerations

- Horizontally scale the application by running multiple instances behind a load balancer.
- Ensure RabbitMQ consumers are idempotent.
- Use shared database or sharded DBs for high load.

---

## AI Usage

- AI assisted in generating documentation, integration tests and code.

---
