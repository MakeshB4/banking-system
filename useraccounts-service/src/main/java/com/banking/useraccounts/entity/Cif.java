package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cif")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cif extends BaseEntity {

    private String cifNumber;

    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType = CustomerType.INDIVIDUAL;

    @Enumerated(EnumType.STRING)
    private CifStatus cifStatus = CifStatus.PENDING;


    private String riskCategory = "LOW";


    private LocalDate activationDate;


    private LocalDate closureDate;


    private LocalDate lastReviewDate;


    private LocalDate nextReviewDate;


    private String approvedBy;


    private LocalDate approvedDate;


    private String remarks;

    public enum CustomerType {
        INDIVIDUAL,
        CORPORATE,
        PARTNERSHIP,
        TRUST,
        GOVERNMENT
    }

    public enum CifStatus {
        PENDING,
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        CLOSED,
        REJECTED
    }
}
