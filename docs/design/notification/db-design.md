# MS3: Notifications Service - Database Design

## Overview
Simple H2 in-memory database schema for notifications.

## Database Information
- **Database**: H2 (In-Memory)
- **Mode**: MySQL compatibility mode
- **URL**: `jdbc:h2:mem:notifications_db`

---

## Table: `notifications`

### Schema

| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| notification_id | BIGINT (PK, Auto) | Unique identifier |
| user_id | BIGINT | User reference |
| type | VARCHAR(10) | EMAIL or SMS |
| recipient | VARCHAR(255) | Email or phone number |
| subject | VARCHAR(500) | Email subject (null for SMS) |
| message | TEXT | Notification content |
| sent | BOOLEAN | Delivery status |
| sent_time | TIMESTAMP | When sent |
| del_flg | BOOLEAN | Soft delete flag |
| creation_time | TIMESTAMP | Created at |
| created_by | VARCHAR(100) | Created by service/user |

### DDL Statement

```sql
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    message TEXT NOT NULL,
    sent BOOLEAN DEFAULT FALSE,
    sent_time TIMESTAMP,
    del_flg BOOLEAN DEFAULT FALSE,
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL
);
