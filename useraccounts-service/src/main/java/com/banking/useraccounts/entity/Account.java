package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountNumberSeq;

    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING;

    private String branchCode;
    private String ifscCode;
    private LocalDate openingDate;
    private LocalDate activationDate;
    private LocalDate closureDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal minimumBalance = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    private String approvedBy;
    private LocalDate approvedDate;
    private String rejectionReason;

    @PostPersist
    public void generateAccountNumber() {
        if (this.accountNumberSeq != null) {
            this.accountNumber = String.format("%012d", this.accountNumberSeq);
        }
    }

    public enum AccountType {
        SAVINGS,
        CURRENT,
        FIXED_DEPOSIT,
        RECURRING_DEPOSIT,
        SALARY
    }

    public enum AccountStatus {
        PENDING,
        ACTIVE,
        INACTIVE,
        FROZEN,
        CLOSED,
        REJECTED
    }
}