package com.banking.notifications.service;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.entity.Notification;
import com.banking.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final AsyncNotificationSender asyncNotificationSender;
    private final NotificationSenderService notificationSenderService; // New injection

    @Override
    @Transactional
    public NotificationResponseDTO sendNotification(NotificationDTO dto) {
        log.info("Sending notification to user: {}, type: {}", dto.getUserId(), dto.getType());

        // Validate notification type is supported
        if (!notificationSenderService.isSupported(dto.getType())) {
            throw new IllegalArgumentException("Unsupported notification type: " + dto.getType());
        }

        // Step 1: Save notification to database
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
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

        log.info("Found {} unsent notifications for user: {}", notifications.size(), userId);

        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    private NotificationResponseDTO mapToResponseDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUserId());
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