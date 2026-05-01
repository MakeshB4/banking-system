package com.banking.notifications.service;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import com.banking.notifications.exceptions.InvalidNotificationTypeException;
import com.banking.notifications.exceptions.InvalidRecipientException;
import com.banking.notifications.exceptions.NotificationNotFoundException;
import com.banking.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSenderService notificationSenderService;

    @Mock
    private AsyncNotificationSender asyncNotificationSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationDTO emailNotificationDTO;
    private NotificationDTO smsNotificationDTO;
    private Notification emailNotification;
    private Notification smsNotification;

    @BeforeEach
    void setUp() {

        emailNotificationDTO = new NotificationDTO();
        emailNotificationDTO.setUserId(1001L);
        emailNotificationDTO.setType(NotificationType.EMAIL);
        emailNotificationDTO.setRecipient("user@example.com");
        emailNotificationDTO.setSubject("Test Subject");
        emailNotificationDTO.setMessage("Test Message");
        emailNotificationDTO.setCreatedBy("admin");

        smsNotificationDTO = new NotificationDTO();
        smsNotificationDTO.setUserId(1001L);
        smsNotificationDTO.setType(NotificationType.SMS);
        smsNotificationDTO.setRecipient("+919876543210");
        smsNotificationDTO.setMessage("Your OTP is 123456");
        smsNotificationDTO.setCreatedBy("system");

        emailNotification = new Notification();
        emailNotification.setId(1L);
        emailNotification.setUserId(1001L);
        emailNotification.setType(NotificationType.EMAIL);
        emailNotification.setRecipient("user@example.com");
        emailNotification.setSubject("Test Subject");
        emailNotification.setMessage("Test Message");
        emailNotification.setSent(false);
        emailNotification.setCreatedBy("admin");
        emailNotification.setCreationTime(LocalDateTime.now());

        smsNotification = new Notification();
        smsNotification.setId(2L);
        smsNotification.setUserId(1002L);
        smsNotification.setType(NotificationType.SMS);
        smsNotification.setRecipient("+919876543210");
        smsNotification.setMessage("Your OTP is 123456");
        smsNotification.setSent(false);
        smsNotification.setCreatedBy("system");
        smsNotification.setCreationTime(LocalDateTime.now());
    }

    @Test
    void sendNotification_EmailNotification_Success() {

        when(notificationSenderService.isSupported(NotificationType.EMAIL)).thenReturn(true);

        Notification firstSave = new Notification();
        firstSave.setId(1L);
        firstSave.setUserId(1001L);
        firstSave.setType(NotificationType.EMAIL);
        firstSave.setRecipient("user@example.com");
        firstSave.setSubject("Test Subject");
        firstSave.setMessage("Test Message");
        firstSave.setSent(false);
        firstSave.setCreatedBy("admin");

        Notification secondSave = new Notification();
        secondSave.setId(1L);
        secondSave.setNotificationId(1L);
        secondSave.setUserId(1001L);
        secondSave.setType(NotificationType.EMAIL);
        secondSave.setRecipient("user@example.com");
        secondSave.setSubject("Test Subject");
        secondSave.setMessage("Test Message");
        secondSave.setSent(false);
        secondSave.setCreatedBy("admin");

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        doNothing().when(asyncNotificationSender).sendAsync(any(Notification.class));

        NotificationResponseDTO result = notificationService.sendNotification(emailNotificationDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNotificationId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1001L);
        assertThat(result.getType()).isEqualTo(NotificationType.EMAIL);
        assertThat(result.getRecipient()).isEqualTo("user@example.com");
        assertThat(result.getSubject()).isEqualTo("Test Subject");
        assertThat(result.getMessage()).isEqualTo("Test Message");
        assertThat(result.getSent()).isFalse();
        assertThat(result.getCreatedBy()).isEqualTo("admin");


        verify(notificationSenderService, times(1)).isSupported(NotificationType.EMAIL);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(asyncNotificationSender, times(1)).sendAsync(any(Notification.class));
    }

    @Test
    void sendNotification_SMSNotification_Success() {
        
        when(notificationSenderService.isSupported(NotificationType.SMS)).thenReturn(true);

        Notification firstSave = new Notification();
        firstSave.setId(2L);
        firstSave.setUserId(1002L);
        firstSave.setType(NotificationType.SMS);
        firstSave.setRecipient("+111222333");
        firstSave.setMessage("Your OTP is 123456");
        firstSave.setSent(false);
        firstSave.setCreatedBy("system");

        Notification secondSave = new Notification();
        secondSave.setId(2L);
        secondSave.setNotificationId(2L);
        secondSave.setUserId(1002L);
        secondSave.setType(NotificationType.SMS);
        secondSave.setRecipient("+111222333");
        secondSave.setMessage("Your OTP is 123456");
        secondSave.setSent(false);
        secondSave.setCreatedBy("system");

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        doNothing().when(asyncNotificationSender).sendAsync(any(Notification.class));

        NotificationResponseDTO result = notificationService.sendNotification(smsNotificationDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getNotificationId()).isEqualTo(2L);
        assertThat(result.getUserId()).isEqualTo(1002L);
        assertThat(result.getType()).isEqualTo(NotificationType.SMS);
        assertThat(result.getRecipient()).isEqualTo("+111222333");
        assertThat(result.getSubject()).isNull();
        assertThat(result.getMessage()).isEqualTo("Your OTP is 123456");
        assertThat(result.getSent()).isFalse();
        assertThat(result.getCreatedBy()).isEqualTo("system");

        verify(notificationSenderService, times(1)).isSupported(NotificationType.SMS);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(asyncNotificationSender, times(1)).sendAsync(any(Notification.class));
    }

    @Test
    void sendNotification_UnsupportedNotificationType_ThrowsException() {

        when(notificationSenderService.isSupported(NotificationType.EMAIL)).thenReturn(false);

        assertThatThrownBy(() -> notificationService.sendNotification(emailNotificationDTO))
                .isInstanceOf(InvalidNotificationTypeException.class)
                .hasMessageContaining("Unsupported notification type: EMAIL");

        verify(notificationSenderService, times(1)).isSupported(NotificationType.EMAIL);
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(asyncNotificationSender, never()).sendAsync(any(Notification.class));
    }

    @Test
    void sendNotification_InvalidEmailFormat_ThrowsException() {

        emailNotificationDTO.setRecipient("invalid-email");
        when(notificationSenderService.isSupported(NotificationType.EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> notificationService.sendNotification(emailNotificationDTO))
                .isInstanceOf(InvalidRecipientException.class)
                .hasMessageContaining("Invalid email format: invalid-email");

        verify(notificationSenderService, times(1)).isSupported(NotificationType.EMAIL);
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(asyncNotificationSender, never()).sendAsync(any(Notification.class));
    }

    @Test
    void sendNotification_InvalidPhoneFormat_ThrowsException() {

        smsNotificationDTO.setRecipient("123");
        when(notificationSenderService.isSupported(NotificationType.SMS)).thenReturn(true);

        assertThatThrownBy(() -> notificationService.sendNotification(smsNotificationDTO))
                .isInstanceOf(InvalidRecipientException.class)
                .hasMessageContaining("Invalid phone number format");

        verify(notificationSenderService, times(1)).isSupported(NotificationType.SMS);
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(asyncNotificationSender, never()).sendAsync(any(Notification.class));
    }

    @Test
    void sendNotification_EmptyRecipient_ThrowsException() {

        emailNotificationDTO.setRecipient("   ");
        when(notificationSenderService.isSupported(NotificationType.EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> notificationService.sendNotification(emailNotificationDTO))
                .isInstanceOf(InvalidRecipientException.class)
                .hasMessageContaining("Invalid email format");

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void getUnsentNotificationsByUser_NullUserId_ThrowsNotificationNotFoundException() {

        when(notificationRepository.findByUserIdAndSentFalseAndDelFlgFalse(null))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> notificationService.getUnsentNotificationsByUser(null))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining("No unsent notifications found for user null");

        verify(notificationRepository, times(1))
                .findByUserIdAndSentFalseAndDelFlgFalse(null);
    }

    @Test
    void getUnsentNotificationsByUser_WithSingleNotification_ReturnsSingleNotification() {

        Long userId = 1001L;
        List<Notification> notifications = Collections.singletonList(emailNotification);

        when(notificationRepository.findByUserIdAndSentFalseAndDelFlgFalse(userId))
                .thenReturn(notifications);

        List<NotificationResponseDTO> result = notificationService.getUnsentNotificationsByUser(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUserId()).isEqualTo(1001L);
        assertThat(result.get(0).getType()).isEqualTo(NotificationType.EMAIL);
        assertThat(result.get(0).getSent()).isFalse();

        verify(notificationRepository, times(1))
                .findByUserIdAndSentFalseAndDelFlgFalse(userId);
    }
}