package com.banking.notifications.dto;

import com.banking.notifications.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for incoming notification requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;  // EMAIL or SMS

    @NotBlank(message = "Recipient is required")
    private String recipient;  // email or phone number

    private String subject;  // optional, mainly for emails

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}