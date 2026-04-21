package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    private String cifNumber;

    private String firstName;

    private String middleName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String email;

    private String mobileNumber;

    private String alternateMobile;

    private String nationality;

    private String maritalStatus;

    private String occupation;

    private Double annualIncome;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status = CustomerStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus = KycStatus.PENDING;

    private String approvedBy;

    private LocalDate approvedDate;

    private String rejectionReason;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private KycDetails kycDetails;

    public enum CustomerStatus {
        PENDING,
        ACTIVE,
        REJECTED,
        SUSPENDED,
        CLOSED
    }

    public enum KycStatus {
        PENDING,
        VERIFIED,
        REJECTED,
        INCOMPLETE
    }
}
