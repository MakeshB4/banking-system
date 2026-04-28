package com.banking.payments.entity;

import com.banking.payments.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    
    private BigDecimal amount;
    private String currency;
    private String senderAccount;
    private String receiverAccount;
    private String senderName;
    private String receiverName;
    private LocalDateTime transactionDate;
    private String status;
    private String referenceNumber;
    private String description;
    private String bankCode;
    private BigDecimal transactionFee;
    private BigDecimal exchangeRate;
}