# Payments Service

Microservice for handling payments in the banking system. Supports payments.

## Features

- Supports Payment within bank and outside bank
- REST API with Swagger docs

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
```bash
git clone <repo-url>
cd payments-service
```

2. Update database config in `application.yml`

3. Run the app
```bash
mvn spring-boot:run
```

The service will start on port 8082 (configurable in application.yml)

## API Endpoints

### do payment
```
POST /api/notifications/send
```

Request body:
```json
{
  "userId": 1,
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Test Notification",
  "message": "This is a test message",
  "createdBy": "system"
}
```

### Get Unsent Notifications
```
GET /api/notifications/getUnsendNotificationById/{userId}
```

## API Documentation

Swagger UI available at: `http://localhost:8082/swagger-ui.html`

## TODO

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

Currently notifications are just logged to console. Need to integrate with actual email/SMS providers.

The async executor uses a thread pool configured in AsyncConfig.java - adjust pool sizes based on load.
