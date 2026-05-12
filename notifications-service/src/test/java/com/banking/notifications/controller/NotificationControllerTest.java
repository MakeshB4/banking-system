package com.banking.notifications.controller;

import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.entity.NotificationType;
import com.banking.notifications.exceptions.InvalidRecipientException;
import com.banking.notifications.exceptions.NotificationNotFoundException;
import com.banking.notifications.service.INotificationService;
import com.banking.notifications.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NotificationController.class)
@AutoConfigureDataJpa
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private INotificationService notificationService;

    @MockBean
    private JwtService jwtService;

    @Test
    void sendNotification_EmailNotification_Success() throws Exception {

        NotificationDTO requestDTO = createEmailNotificationDTO();
        NotificationResponseDTO responseDTO = createEmailNotificationResponseDTO();

        when(notificationService.sendNotification(any(NotificationDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.notificationId").value(1))
                .andExpect(jsonPath("$.data.userId").value(1001))
                .andExpect(jsonPath("$.data.type").value("EMAIL"))
                .andExpect(jsonPath("$.data.recipient").value("user@example.com"))
                .andExpect(jsonPath("$.data.subject").value("Test Subject"))
                .andExpect(jsonPath("$.data.message").value("Test Message"))
                .andExpect(jsonPath("$.data.sent").value(false))
                .andExpect(jsonPath("$.data.createdBy").value("admin"));

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void sendNotification_SMSNotification_Success() throws Exception {

        NotificationDTO requestDTO = createSMSNotificationDTO();
        NotificationResponseDTO responseDTO = createSMSNotificationResponseDTO();

        when(notificationService.sendNotification(any(NotificationDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.userId").value(1002L))
                .andExpect(jsonPath("$.data.type").value(String.valueOf(NotificationType.SMS)))
                .andExpect(jsonPath("$.data.recipient").value("+121111111"))
                .andExpect(jsonPath("$.data.message").value("Your OTP is 123456"))
                .andExpect(jsonPath("$.data.sent").value(false))
                .andExpect(jsonPath("$.data.createdBy").value("system"));

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }


    @Test
    void sendNotification_EmailNotification_InvalidReceipt_throwsInvalidRecipientException() throws Exception {
        Long userId = 1000L;

        when(notificationService.getUnsentNotificationsByUser(userId))
                .thenThrow(new InvalidRecipientException(
                        String.format("Invalid email format:  %d", userId)
                ));

        mockMvc.perform(get("/api/v1/notifications/getUnsendNotificationById/{userId}", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid email format:  1000"));

        verify(notificationService, times(1)).getUnsentNotificationsByUser(userId);
    }

    @Test
    void getUnsendNotificationById_NoRecordFound_throws_NotificationNotFoundException() throws Exception {

        Long userId = 9999L;

        when(notificationService.getUnsentNotificationsByUser(userId))
                .thenThrow(new NotificationNotFoundException(
                        String.format("No unsent notifications found for user %d", userId)
                ));

        mockMvc.perform(get("/api/v1/notifications/getUnsendNotificationById/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No unsent notifications found for user 9999"));

        verify(notificationService, times(1)).getUnsentNotificationsByUser(userId);
    }

    @Test
    void getUnsendNotificationById_WithMultipleNotifications_Success() throws Exception {

        Long userId = 1001L;
        List<NotificationResponseDTO> notifications = Arrays.asList(
                createEmailNotificationResponseDTO(),
                createSMSNotificationResponseDTO()
        );

        when(notificationService.getUnsentNotificationsByUser(userId))
                .thenReturn(notifications);


        mockMvc.perform(get("/api/v1/notifications/getUnsendNotificationById/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Retrieved 2 unsent notification(s)"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].sent").value(false))
                .andExpect(jsonPath("$.data[1].sent").value(false));

        verify(notificationService, times(1)).getUnsentNotificationsByUser(userId);
    }

    private NotificationDTO createEmailNotificationDTO() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1001L);
        dto.setType(NotificationType.EMAIL);
        dto.setRecipient("user@example.com");
        dto.setSubject("Test Subject");
        dto.setMessage("Test Message");
        dto.setCreatedBy("admin");
        return dto;
    }

    private NotificationDTO createSMSNotificationDTO() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1002L);
        dto.setType(NotificationType.SMS);
        dto.setRecipient("+121111111");
        dto.setMessage("Your OTP is 123456");
        dto.setCreatedBy("system");
        return dto;
    }

    private NotificationResponseDTO createEmailNotificationResponseDTO() {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(1L);
        dto.setNotificationId(1L);
        dto.setUserId(1001L);
        dto.setType(NotificationType.EMAIL);
        dto.setRecipient("user@example.com");
        dto.setSubject("Test Subject");
        dto.setMessage("Test Message");
        dto.setSent(false);
        dto.setSentTime(null);
        dto.setCreationTime(LocalDateTime.now());
        dto.setCreatedBy("admin");
        return dto;
    }

    private NotificationResponseDTO createSMSNotificationResponseDTO() {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(2L);
        dto.setNotificationId(2L);
        dto.setUserId(1002L);
        dto.setType(NotificationType.SMS);
        dto.setRecipient("+121111111");
        dto.setSubject(null);
        dto.setMessage("Your OTP is 123456");
        dto.setSent(false);
        dto.setSentTime(null);
        dto.setCreationTime(LocalDateTime.now());
        dto.setCreatedBy("system");
        return dto;
    }

}