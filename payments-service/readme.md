# Payments Service

Microservice for handling payments in the banking system. Supports domestic, international, and within-bank transfers.

## Features

- Payment processing for three types:
  - **Within Bank**: Transfers between accounts in the same bank
  - **Domestic**: Transfers to other banks within the country (IFSC)
  - **International**: Cross-border payments (SWIFT)
- Transaction status tracking
- REST API with Swagger documentation

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- MySQL
- Lombok
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- JDK 21+
- Maven 3.9+
- MySQL 8.0+

### Setup

1. Clone the repo
git clone https://github.com/MakeshB4/banking-system.git
cd payments-service


2. Update database config in `application.yml`

3. Run the app
mvn spring-boot:run


The service will start on port 8082 (configurable in application.yml)

## API Endpoints
```
POST api/v1/payments/createPayment
GET /status/{transactionId}

```

## API Documentation

Swagger UI available at: `http://localhost:8082/payments-service/swagger-ui/index.html`

## Future Implementation

- [ ] Integrate actual core banking payment Processing service for real Time processing
- [ ] Support Scheduled payments

## Configuration

Key configs in `application.yml`:

- Database connection
- Thread pool settings
- Server port

## Notes

Currently notifications are just logged to console. and stored in DB Need to integrate with actual email/SMS providers.

The async executor uses a thread pool configured in AsyncConfig.java - adjust pool sizes based on load.