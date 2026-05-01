package com.banking.useraccounts.client;

import com.banking.useraccounts.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8083/notification-service/api/notifications/send";

    public void sendNotification(Customer customerNumber, Long cif) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("userId", customerNumber.getId());
        notificationPayload.put("type", "EMAIL");
        notificationPayload.put("recipient", customerNumber.getEmail());
        notificationPayload.put("subject", "User Registration Notification");
        notificationPayload.put("message", "User Registered  with Customer Number " + customerNumber.getId() + "and cif Id" + cif);
        notificationPayload.put("createdBy", customerNumber.getFirstName());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(notificationPayload, headers);

        try {
            restTemplate.postForEntity(NOTIFICATION_SERVICE_URL, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending notification  with " + customerNumber.getId()+ "and cif Id" + customerNumber.getId());
        }
    }
}