package com.banking.notifications.strategy;

import com.banking.notifications.entity.Notification;

public interface NotificationStrategy {


    void send(Notification notification);

    com.banking.notifications.entity.NotificationType getType();
}