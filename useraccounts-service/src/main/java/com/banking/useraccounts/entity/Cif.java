package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cif")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = {"customer", "accounts"})
@NoArgsConstructor
@AllArgsConstructor
public class Cif extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_number", unique = true, nullable = false)
    private Long customerNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cif", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type")
    private CustomerType customerType = CustomerType.INDIVIDUAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "cif_status")
    private CifStatus cifStatus = CifStatus.PENDING;

    @Column(name = "risk_category")
    private String riskCategory = "LOW";

    @Column(name = "activation_date")
    private LocalDate activationDate;

    @Column(name = "closure_date")
    private LocalDate closureDate;

    @Column(name = "last_review_date")
    private LocalDate lastReviewDate;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    @Column(name = "remarks", length = 500)
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