# User Accounts Service

Microservice for User registration,Account creation,Enabling user for banking system by approving the registration,
Registering the user for authentication which is used for token generation.

## Features

- User registration
- Account creation
- Approve user registration
- Add user for token generation
- Token generation for Authentication 
- REST API with Swagger docs

## Tech Stack

- Java 21
- Spring Boot 3.9
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
```bash
git clone https://github.com/MakeshB4/banking-system.git
cd useraccounts-service
```

2. Update database config in `application.yml`

3. Run the app
```bash
mvn spring-boot:run
```

The service will start on port 8081 (configurable in application.yml)

## API Endpoints

```
POST useraccounts-service/api/v1/auth/register
POST useraccounts-service/api/v1/auth/login
POST useraccounts-service/api/v1/users/register
GET  useraccounts-service/api/v1/users/pending/{cif-number}
PUT useraccounts-service/api/v1/users/update
```

## API Documentation

Swagger UI available at: `http://localhost:8081/useraccounts-service/swagger-ui/index.html`

## Future Implementation

- [ ] Adding accounts to registered User's Cif
- [ ] Integration with Adhaar and PAN System API's for real time verification

## Configuration

Key configs in `application.yml`:

- Database connection
- Thread pool settings
- Server port

## Notes

Currently  user can register only for current or savings account and  it should be enabled by  Bank Admin.
