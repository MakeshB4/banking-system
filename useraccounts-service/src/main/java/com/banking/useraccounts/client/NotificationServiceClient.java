package com.banking.useraccounts.client;

import com.banking.useraccounts.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;


    public void sendNotification(Customer customerNumber, Long cif,String jwtToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);

        System.out.println("Authorization header: " + jwtToken);


        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("userId", customerNumber.getId());
        notificationPayload.put("type", "EMAIL");
        notificationPayload.put("recipient", customerNumber.getEmail());
        notificationPayload.put("subject", "User Registration Notification");
        notificationPayload.put("message", "User Registered  with Customer Number " + customerNumber.getId() + "and cif Id" + cif);
        notificationPayload.put("createdBy", customerNumber.getFirstName());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(notificationPayload, headers);

        try {
            restTemplate.postForEntity(notificationServiceUrl, requestEntity, String.class);
        } catch (Exception e) {
            log.error("Error while sending notification  with " + customerNumber.getId()+ "and cif Id" + customerNumber.getId());
        }
    }

}