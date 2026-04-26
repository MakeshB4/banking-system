package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = {"cif"})
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cif_id", nullable = false)
    private Cif cif;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Column(name = "branch_code", length = 10)
    private String branchCode;

    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "status")
    private String status = "ACTIVE";
}