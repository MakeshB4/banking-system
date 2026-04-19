package com.banking.notifications.service;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.entity.NotificationType;

import java.util.List;

public interface INotificationService {

    /**
     * Send a notification (create and mark as sent)
     */
    NotificationResponseDTO sendNotification(NotificationDTO dto);

    /**
     * Get All unsend notification by User id
     */
    List<NotificationResponseDTO> getUnsentNotificationsByUser(Long userId);

}