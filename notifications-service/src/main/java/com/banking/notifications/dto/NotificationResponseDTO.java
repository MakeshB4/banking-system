package com.banking.notifications.dto;

import com.banking.notifications.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private Long notificationId;
    private String userName;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String message;
    private Boolean sent;
    private LocalDateTime sentTime;
    private LocalDateTime creationTime;
    private String createdBy;
}