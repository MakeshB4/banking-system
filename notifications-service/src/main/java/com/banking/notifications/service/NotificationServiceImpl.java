package com.banking.notifications.service;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import com.banking.notifications.exceptions.InvalidNotificationTypeException;
import com.banking.notifications.exceptions.InvalidRecipientException;
import com.banking.notifications.exceptions.NotificationNotFoundException;
import com.banking.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final AsyncNotificationSender asyncNotificationSender;
    private final NotificationSenderService notificationSenderService; // New injection


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    @Override
    @Transactional
    public NotificationResponseDTO sendNotification(NotificationDTO dto) {
        log.info("Sending notification to user: {}, type: {}", dto.getUserName(), dto.getType());

        // Validate notification type is supported
        if (!notificationSenderService.isSupported(dto.getType())) {
            throw new InvalidNotificationTypeException("Unsupported notification type: " + dto.getType());
        }

        validateRecipient(dto.getType(), dto.getRecipient());

        // Step 1: Save notification to database
        Notification notification = new Notification();
        notification.setUserName(dto.getUserName());
        notification.setType(dto.getType());
        notification.setRecipient(dto.getRecipient());
        notification.setSubject(dto.getSubject());
        notification.setMessage(dto.getMessage());
        notification.setSent(false); // Initially not sent
        notification.setCreatedBy(dto.getCreatedBy());

        Notification saved = notificationRepository.save(notification);
        saved.setNotificationId(saved.getId());
        saved = notificationRepository.save(saved);

        log.info("Notification saved with ID: {}, triggering async send", saved.getId());

        // Send asynchronously
        asyncNotificationSender.sendAsync(saved);

       // Return response immediately
        return mapToResponseDTO(saved);
    }

    @Override
    public List<NotificationResponseDTO> getUnsentNotificationsByUser(Long userId) {
        log.info("Fetching unsent notifications for user: {}", userId);

        List<Notification> notifications = notificationRepository.findByUserIdAndSentFalseAndDelFlgFalse(userId);

        if(notifications.isEmpty()){
            log.warn("No unsent notifications found for user: {}", userId);
            throw new NotificationNotFoundException(
                    String.format("No unsent notifications found for user %d", userId)
            );
        }

        log.info("Found {} unsent notifications for user: {}", notifications.size(), userId);

        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    private void validateRecipient(NotificationType type, String recipient) {
        System.out.println("Recipient: [" + recipient + "]");
        System.out.println("Trimmed: [" + recipient.trim() + "]");
        System.out.println("Matches: " + EMAIL_PATTERN.matcher(recipient.trim()).matches());
        if (type == NotificationType.EMAIL) {
            if (!EMAIL_PATTERN.matcher(recipient.trim()).matches()) {
                throw new InvalidRecipientException("Invalid email format: " + recipient);
            }
        } else if (type == NotificationType.SMS) {
            String cleanPhone = recipient.replaceAll("[\\s\\-()]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new InvalidRecipientException("Invalid phone number format: " + recipient);
            }
        }
    }


    private NotificationResponseDTO mapToResponseDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserName(notification.getUserName());
        dto.setType(notification.getType());
        dto.setRecipient(notification.getRecipient());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setSent(notification.getSent());
        dto.setSentTime(notification.getSentTime());
        dto.setCreationTime(notification.getCreationTime());
        dto.setCreatedBy(notification.getCreatedBy());
        return dto;
    }
}