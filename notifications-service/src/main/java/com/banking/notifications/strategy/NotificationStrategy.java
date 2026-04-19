package com.banking.notifications.strategy;

import com.banking.notifications.entity.Notification;

public interface NotificationStrategy {
    
    /**
     * Send notification (currently logs, Phase 2 will use messaging)
     */
    void send(Notification notification);
    
    /**
     * Get the notification type this strategy handles
     */
    com.banking.notifications.entity.NotificationType getType();
}