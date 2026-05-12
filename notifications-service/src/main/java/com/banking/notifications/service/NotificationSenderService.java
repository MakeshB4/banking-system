package com.banking.notifications.service;

import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import com.banking.notifications.factory.NotificationFactory;
import com.banking.notifications.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSenderService {

    private final NotificationFactory notificationFactory;


    public void send(Notification notification) {
        try {
            log.info("Sending notification ID: {} of type: {}",
                    notification.getId(), notification.getType());

            NotificationStrategy strategy = notificationFactory.getStrategy(notification.getType());
            strategy.send(notification);

            log.info("Notification ID: {} sent successfully via {}",
                    notification.getId(), notification.getType());

        } catch (Exception e) {
            log.error("Failed to send notification ID: {}, type: {}",
                    notification.getId(), notification.getType(), e);
            throw new RuntimeException("Notification sending failed", e);
        }
    }


    public boolean isSupported(NotificationType type) {
        try {
            notificationFactory.getStrategy(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}