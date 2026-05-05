package com.banking.notifications.strategy;

import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(Notification notification) {
        log.info("=== EMAIL NOTIFICATION ===");
        log.info("To: {}", notification.getRecipient());
        log.info("Subject: {}", notification.getSubject());
        log.info("Message: {}", notification.getMessage());
        log.info("User ID: {}", notification.getUserId());
        log.info("Notification ID: {}", notification.getNotificationId());
        log.info("=========================");

       /* // TODO: integrate with email service provider (SendGrid/AWS SES)
        // TODO: or send to Kafka/RabbitMQ for processing */
    }

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }
}