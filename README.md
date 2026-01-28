# Versioned Key-Value Store (AsiaKV)

# AsiaKV – Versioned Key-Value Store

![CI](https://github.com/khomeini-air/asiakv/actions/workflows/main.yml/badge.svg)
(https://github.com/khomeini-air/asiakv/actions/workflows/main.yml)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)


AsiaKV is a **version-controlled key-value store** that supports concurrent updates, historical reads by timestamp, and pagination over the latest values.

---

## Features

* Versioned key - json blob value storage.
* Strict version incrementing (`+1`) per update.
* Safe handling of high concurrency, e.g. 50 concurrent writes.
* Time-travel queries (read value at a given timestamp).
* Pagination for listing latest values.
* Database-backed consistency using row-level locking
* Unit & integration tests
* Code coverage reporting via JaCoCo

---

## Architecture Overview

The system is implemented using **Spring Boot + Spring Data JPA** with a relational database.

### Technology Stack

* Java 17
* Spring Boot 3 with Swagger/OpenAPI 3.0 and Actuator-Prometheus integration.
* Spring Data JPA
* Hibernate
* PostgreSQL
* JUnit
* Mockito
* JaCoCo
* CI/CD with Github Actions.

### Component Structure

```
src/main/java/com/asia/asiakv
├── object            # Object domain: contains all business logic related to the versioned key-value store.
│   ├── api
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── mapper
│   └── validator
│
└── shared            # Shared domain: Contains cross-cutting concerns reused across domains.
    ├── config
    ├── filter
    ├── exception
    ├── enums
    ├── mapper
    └── dto
```
### Core Tables

1. `key_value`

Stores all historical versions of a key.

| Column      | Description                  |
| ----------- |------------------------------|
| `id`        | Primary key                  |
| `key`       | Logical key (string)         |
| `version`   | Version number (starts at 1) |
| `value`     | JSON Blob value              |
| `timestamp` | UNIX timestamp (UTC)         |

2.  `key_current_version`

Tracks the **latest version per key**.

| Column            | Description    |
| ----------------- | -------------- |
| `key`             | Logical key    |
| `current_version` | Latest version |


---
## Getting Started
### Prerequisites
- Docker
- Docker Compose

### Running with Docker
1. Clone the project into you local environment.
2. Go to the project directory and run the docker command
```bash
docker compose up --build
```

The service will be available at:

```
http://localhost:8080
```
### Access Points

| Endpoint                                    | Purpose                       | Authentication |
|---------------------------------------------|-------------------------------|----------------|
| http://localhost:8080/objects               | Optimize load API             | None (public)  |
| http://localhost:8080/swagger-ui/index.html | Interactive API Documentation | None (public)  |
| http://localhost:8080/v3/api-docs           | OpenAPI Specification (JSON)  | None (public)  |
| http://localhost:8080/actuator/health       | Health Check                  | None (public)  |
| http://localhost:8080/actuator/info         | Application Info              | None (public)     |
| http://localhost:8080/actuator/metrics      | Metrics                       | None (public)     |
| http://localhost:8080/actuator/prometheus   | Prometheus Metrics            | None (public)     |

## API Endpoints

Refer to swagger-ui for interactive API doc: **`/swagger-ui/index.html`**

### 1. Create or Update a Key
Create a new Key or update it if it exists. The old versions and its value are also preserved, creating a versioning history of the key value.

**POST** `/objects`

**Request**:
```json
{
  "jsonkey": {
    "goal": "president",
    "istrue": true
  }
}
```

**Response**:
```json
{
  "result": {
    "result": "S",
    "code": "SUCCESS",
    "description": "Success"
  },
  "data": {
    "key": "jsonkey",
    "value": {
      "goal": "president",
      "istrue": true
    },
    "version": 10,
    "timestamp": 1769487461629
  }
}
```
**Rules**

* If the key does not exist → create with `version = 1`
* If the key exists → update it to the new value and increment version by exactly `+1`. The old value and its version is preserved 

---

### 2. Get Latest Value for a Key

**GET** `/objects/{key}`

**Response**

```json
{
  "result": {
    "result": "S",
    "code": "SUCCESS",
    "description": "Success"
  },
  "data": {
    "key": "jsonkey",
    "value": {
      "goal": "president",
      "isValid": true
    },
    "version": 10,
    "timestamp": 1769487461629
  }
}
```

---

### 3. Get Value at a Specific Timestamp
Returns the value that was valid at the given UNIX timestamp (UTC).

**GET** `/objects/{key}?timestamp=[timestamp]`

**Response**
```json
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "data": {
        "key": "jsonkey",
        "value": {
            "goal": "president",
            "isValid": true
        },
        "version": 7,
        "timestamp": 1769440819635
    }
}
```
---

### 4. Get All Latest Records (Paginated)
Returns only the **latest version of each key**, ordered by timestamp descending.

**GET** `/objects?page=0&size=5&sortDirection=DESCENDING&sortField=TIMESTAMP`  


**Response:**
```json
{
    "result": {
        "result": "S",
        "code": "SUCCESS",
        "description": "Success"
    },
    "data": [
        {
            "key": "key space",
            "value": {
                "name": "John Rambo",
                "isVeteran": true
            },
            "version": 1,
            "timestamp": 1769488091362
        },
        {
            "key": "jsonkey",
            "value": {
                "goal": "president",
                "isValid": true
            },
            "version": 10,
            "timestamp": 1769487461629
        },
        {
            "key": "key3",
            "value": "value5",
            "version": 5,
            "timestamp": 1769440790526
        }
    ],
    "pagination": {
        "currentPage": 0,
        "pageSize": 5,
        "totalElements": 3,
        "totalPages": 1
    }
}
```
Valid value for sorting:
- `sortDirection: DESCENDING | ASCENDING`  
- `sortField: KEY | TIMESTAMP`


---

## CI/CD

This project implements CI/CD using GitHub Actions.

However, due to recent changes in free-tier policies of popular PaaS providers
(Fly.io, Render, Koyeb, etc), deployment will require a credit card, which I do not possess.

Hence, the deployment is simulated through Docker-based packaging, ensuring
the application can be deployed unchanged to any container platform.

Any push to the `main` branch triggers:
  - Project build
  - Unit and integration tests
  - JaCoCo code coverage reporting 
  - Build status is visible per commit via GitHub Actions.
  - Upon successful tests, a Docker image is built as a deployable artifact.

---

