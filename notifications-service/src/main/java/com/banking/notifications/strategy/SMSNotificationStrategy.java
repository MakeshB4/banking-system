package com.banking.notifications.strategy;

import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SMSNotificationStrategy implements NotificationStrategy {

    @Override
    public void send(Notification notification) {
        log.info("=== SMS NOTIFICATION ===");
        log.info("To: {}", notification.getRecipient());
        log.info("Message: {}", notification.getMessage());
        log.info("User ID: {}", notification.getUserId());
        log.info("Notification ID: {}", notification.getNotificationId());
        log.info("========================");
        
        // Phase 2: Send to message queue (Kafka/RabbitMQ)
        // messageProducer.send("sms-queue", notification);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }
}