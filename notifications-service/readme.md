# Notifications Service

Microservice for handling notifications in the banking system. Supports Email and SMS notifications.

## Features

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

### Send Notification
```
POST /api/v1/notifications/send
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
GET /api/v1/notifications/getUnsendNotificationById/{userId}
```

## API Documentation

Swagger UI available at: `http://localhost:8083/notification-service/swagger-ui/index.html`

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

Currently notifications are just logged to console. and stored in DB Need to integrate with actual email/SMS providers.

The async executor uses a thread pool configured in AsyncConfig.java - adjust pool sizes based on load.
