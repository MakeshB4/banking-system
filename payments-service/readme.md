# Payments Service

Microservice for handling payments in the banking system. Supports domestic, international, and within-bank transfers.

## Features

- Payment processing for three types:
  - **Within Bank**: Transfers between accounts in the same bank
  - **Domestic**: Transfers to other banks within the country (IFSC)
  - **International**: Cross-border payments (SWIFT)
- Transaction status tracking
- Real-time payment status retrieval
- Audit trail with creation/modification tracking
- REST API with Swagger documentation

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL
- Lombok
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### Setup

1. Clone the repo
git clone <repo-url>
cd payments-service


2. Update database config in `application.yml`

3. Run the app
mvn spring-boot:run


The service will start on port 8082 (configurable in application.yml)

## API Endpoints

### Process Payment