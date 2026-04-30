package com.banking.payments.dto;

import com.banking.payments.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long transactionId;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private Long senderAccount;
    private String receiverAccount;
    private String status;
    private String referenceNumber;
    private LocalDateTime transactionDate;
    private BigDecimal transactionFee;
    private String message;
}