package com.banking.payments.service;

import com.banking.payments.client.AccountServiceClient;
import com.banking.payments.dto.PaymentRequest;
import com.banking.payments.dto.PaymentResponse;
import com.banking.payments.entity.Payment;
import com.banking.payments.enums.PaymentType;
import com.banking.payments.exceptions.InvalidPaymentAmountException;
import com.banking.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest request;
    private Payment payment;

    @BeforeEach
    void setup() {
        request = new PaymentRequest();
        request.setPaymentType(PaymentType.DOMESTIC);
        request.setAmount(new BigDecimal("5000"));
        request.setCurrency("INR");
        request.setSenderAccount(1234567890L);
        request.setReceiverAccount("9876543210");
        request.setSenderName("John");
        request.setReceiverName("Jane");
        request.setBankCode("SBIN0001234");

        payment = new Payment();
        payment.setTransactionId(1L);
        payment.setAmount(new BigDecimal("5000"));
        payment.setStatus("PENDING");
        payment.setReferenceNumber("TXN123ABC");
    }

    @Test
    void processPayment_whenBalanceIsSufficient_shouldCreatePayment() {
        when(accountServiceClient.getAccountBalance(1234567890L)).thenReturn(new BigDecimal("10000"));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_whenBalanceIsLow_shouldThrowException() {
        when(accountServiceClient.getAccountBalance(1234567890L)).thenReturn(new BigDecimal("1000"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void processPayment_withZeroAmount_shouldFail() {
        request.setAmount(BigDecimal.ZERO);
        when(accountServiceClient.getAccountBalance(anyLong())).thenReturn(new BigDecimal("10000"));

        assertThrows(InvalidPaymentAmountException.class, () -> {
            paymentService.processPayment(request);
        });
    }

    @Test
    void processPayment_withNegativeAmount_shouldNotWork() {
        request.setAmount(new BigDecimal("-500"));
        when(accountServiceClient.getAccountBalance(anyLong())).thenReturn(new BigDecimal("10000"));

        InvalidPaymentAmountException ex = assertThrows(InvalidPaymentAmountException.class, () -> {
            paymentService.processPayment(request);
        });
        
        assertEquals("Invalid Payment Amount", ex.getMessage());
    }

    @Test
    void getPaymentStatus_whenPaymentExists_returnsStatus() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentStatus(1L);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void getPaymentStatus_whenPaymentNotFound_throwsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentStatus(999L);
        });
        
        assertEquals("Payment not found", ex.getMessage());
    }

    @Test
    void processPayment_international_shouldWork() {
        request.setPaymentType(PaymentType.INTERNATIONAL);
        request.setCurrency("USD");
        request.setBankCode("CHASUS33");
        
        when(accountServiceClient.getAccountBalance(anyLong())).thenReturn(new BigDecimal("20000"));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
    }

    @Test
    void processPayment_withinBank_noExternalBankCode() {
        request.setPaymentType(PaymentType.WITHIN_BANK);
        request.setBankCode("");
        
        when(accountServiceClient.getAccountBalance(anyLong())).thenReturn(new BigDecimal("8000"));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentResponse result = paymentService.processPayment(request);

        assertNotNull(result);
        verify(accountServiceClient, times(1)).getAccountBalance(1234567890L);
    }

    @Test
    void processPayment_exactBalance_shouldSucceed() {
        when(accountServiceClient.getAccountBalance(1234567890L)).thenReturn(new BigDecimal("5000"));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void processPayment_largeAmount_works() {
        request.setAmount(new BigDecimal("500000"));
        payment.setAmount(new BigDecimal("500000"));
        
        when(accountServiceClient.getAccountBalance(anyLong())).thenReturn(new BigDecimal("1000000"));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentResponse res = paymentService.processPayment(request);

        assertNotNull(res);
    }
}