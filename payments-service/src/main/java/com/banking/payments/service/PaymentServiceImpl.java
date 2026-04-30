package com.banking.payments.service;

import com.banking.payments.client.AccountServiceClient;
import com.banking.payments.dto.PaymentRequest;
import com.banking.payments.dto.PaymentResponse;
import com.banking.payments.entity.Payment;
import com.banking.payments.exceptions.InvalidPaymentAmountException;
import com.banking.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountServiceClient accountServiceClient;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        BigDecimal senderBalance = accountServiceClient.getAccountBalance(request.getSenderAccount());

        if (senderBalance.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in sender account");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException("Invalid Payment Amount");
        }

        Payment payment = new Payment();
        payment.setPaymentType(request.getPaymentType());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setSenderAccount(request.getSenderAccount());
        payment.setReceiverAccount(request.getReceiverAccount());
        payment.setSenderName(request.getSenderName());
        payment.setReceiverName(request.getReceiverName());
        payment.setDescription(request.getDescription());
        payment.setBankCode(request.getBankCode());
        payment.setReferenceNumber(generateReferenceNumber());
        payment.setTransactionDate(LocalDateTime.now());
        payment.setStatus("PENDING");

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment, "Payment initiated successfully");
    }

    @Override
    public PaymentResponse getPaymentStatus(Long transactionId) {
        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return mapToResponse(payment, "Payment status retrieved successfully");
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment, String message) {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentType(payment.getPaymentType());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setSenderAccount(payment.getSenderAccount());
        response.setReceiverAccount(payment.getReceiverAccount());
        response.setStatus(payment.getStatus());
        response.setReferenceNumber(payment.getReferenceNumber());
        response.setTransactionDate(payment.getTransactionDate());
        response.setTransactionFee(payment.getTransactionFee());
        response.setMessage(message);
        return response;
    }
}