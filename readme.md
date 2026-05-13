# Banking Service

# User Accounts Service

Microservice for User registration,Account creation,Enabling user for banking system by approving the registration,
Registering the user for authentication which is used for token generation.

# Payments Service

Microservice for handling payments in the banking system. Supports domestic, international, and within-bank transfers.

# Notifications Service

Microservice for handling notifications in the banking system. Supports Email and SMS notifications.

## Features

- User registration
- Account creation
- Approve user registration
- Add user for token generation
- Token generation for Authentication
- REST API with Swagger docs
- Payment processing for three types:
    - **Within Bank**: Transfers between accounts in the same bank
    - **Domestic**: Transfers to other banks within the country (IFSC)
    - **International**: Cross-border payments (SWIFT)
- Transaction status tracking
- REST API with Swagger documentation
- Send email and SMS notifications
- Async processing with thread pool
- Notification history tracking
- REST API with Swagger **docs**

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- MySQL
- Lombok
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.9
- MySQL 8.0+(Default is h2DB)

### Setup

1. Clone the repo
```bash
git clone https://github.com/MakeshB4/banking-system.git
cd notifications-service
```

2. Update database config in `application.yml`

3. Run the app
```bash
mvn spring-boot:run
```

The service will start on port 8082 (configurable in application.yml)

## API Endpoints

### 
```

POST useraccounts-service/api/v1/auth/register
POST useraccounts-service/api/v1/auth/login
POST useraccounts-service/api/v1/users/register
GET  useraccounts-service/api/v1/users/pending/{cif-number}
PUT useraccounts-service/api/v1/users/update
POST api/v1/payments/createPayment
GET /status/{transactionId
POST /api/v1/notifications/send
GET /api/v1/notifications/getUnsendNotificationById/{userId}

```


## API Documentation

Swagger UI available at: `http://localhost:8081/useraccounts-service/swagger-ui/index.html`
Swagger UI available at: `http://localhost:8082/payments-service/swagger-ui/index.html`
Swagger UI available at: `http://localhost:8083/notification-service/swagger-ui/index.html`

## Future Implementation

- [ ] Adding accounts to registered User's Cif
- [ ] Integration with Adhaar and PAN System API's for real time verification
- [ ] Integrate actual core banking payment Processing service for real Time processing
- [ ] Support Scheduled payments
- [ ] Integrate actual email service (SendGrid/AWS SES)
- [ ] Add SMS provider integration (Twilio)
- [ ] Implement retry mechanism for failed notifications
- [ ] Add notification templates
- [ ] Rate limiting
- [ ] Add pagination for notification list
- [ ] Implement push notifications support

## Configuration

Key configs in `application.yml`:

- Database connection
- Thread pool settings
- Server port

## Notes

Currently  user can register only for current or savings account and  it should be enabled by  Bank Admin.
Currently notifications are just logged to console. and stored in DB Need to integrate with actual email/SMS providers.
The async executor uses a thread pool configured in AsyncConfig.java - adjust pool sizes based on load.