package com.banking.notifications.service;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;

import java.util.List;

public interface INotificationService {


    NotificationResponseDTO sendNotification(NotificationDTO dto);


    List<NotificationResponseDTO> getUnsentNotificationsByUser(Long userId);

}