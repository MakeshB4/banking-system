package com.banking.payments.dto;

import com.banking.payments.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private Long senderAccount;
    private String receiverAccount;
    private String senderName;
    private String receiverName;
    private String description;
    private String bankCode;
    private Long senderId;
    private String email;
    private String phoneNumber;
}