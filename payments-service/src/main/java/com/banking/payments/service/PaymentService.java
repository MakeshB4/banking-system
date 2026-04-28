package com.banking.payments.service;


import com.banking.payments.dto.PaymentRequest;
import com.banking.payments.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentStatus(Long transactionId);
}