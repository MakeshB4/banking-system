package com.banking.notifications.service;

import com.banking.notifications.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationSender {

    private final NotificationSenderService notificationSenderService;

    @Async("notificationExecutor")
    public void sendAsync(Notification notification) {
        try {
            log.info("Async processing notification ID: {}", notification.getId());
            
            notificationSenderService.send(notification);
            
            log.info("Async notification ID: {} processed successfully", notification.getId());
            
        } catch (Exception e) {
            log.error("Async notification ID: {} failed", notification.getId(), e);
            // Phase 2: Update notification status to failed in DB
            // Phase 2: Send to dead letter queue for retry
        }
    }
}