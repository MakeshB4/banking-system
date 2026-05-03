package com.banking.payments.controller;

import com.banking.payments.client.NotificationServiceClient;
import com.banking.payments.dto.PaymentRequest;
import com.banking.payments.dto.PaymentResponse;
import com.banking.payments.enums.PaymentType;
import com.banking.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationServiceClient notificationServiceClient;

    @Test
    void testCreatePayment() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setPaymentType(PaymentType.DOMESTIC);
        request.setAmount(new BigDecimal("5000"));
        request.setCurrency("INR");
        request.setSenderAccount(123456L);
        request.setReceiverAccount("987654");
        request.setSenderName("John");
        request.setReceiverName("Jane");

        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(1L);
        response.setStatus("PENDING");

        doNothing().when(notificationServiceClient)
                .sendNotification(any(PaymentRequest.class), any(),anyString());

        when(paymentService.processPayment(any())).thenReturn(response);

        mockMvc.perform(post("/api/payments/createPayment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetPaymentStatus() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(1L);
        response.setStatus("PENDING");

        doNothing().when(notificationServiceClient)
                .sendNotification(any(PaymentRequest.class), any(),anyString());
        when(paymentService.getPaymentStatus(1L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testPaymentNotFound() throws Exception {
        when(paymentService.getPaymentStatus(999L))
                .thenThrow(new RuntimeException("Payment not found"));

        mockMvc.perform(get("/api/payments/status/999"))
                .andExpect(status().isInternalServerError());
    }
}