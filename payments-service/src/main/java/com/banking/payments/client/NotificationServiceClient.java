package com.banking.payments.client;

import com.banking.payments.dto.AccountBalanceResponse;
import com.banking.payments.dto.PaymentRequest;
import com.banking.payments.entity.Payment;
import com.banking.payments.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public void sendNotification(PaymentRequest paymentRequest,Long transactionId,String jwtToken) {
        AccountBalanceResponse response;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + jwtToken);

        System.out.println("Authorization header: " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("userId", paymentRequest.getSenderId());
        notificationPayload.put("type", "EMAIL");
        notificationPayload.put("recipient", paymentRequest.getEmail());
        notificationPayload.put("subject", "Payment Notification");
        notificationPayload.put("message", "Payment of " + paymentRequest.getAmount() + " " + paymentRequest.getCurrency() + " has been initiated with"+transactionId);
        notificationPayload.put("createdBy", paymentRequest.getSenderName());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(notificationPayload, headers);

        try {
            restTemplate.postForEntity(notificationServiceUrl, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending notification: " + paymentRequest.getSenderName()+" "+paymentRequest.getPaymentType()+" " +paymentRequest.getEmail());
        }
    }
}