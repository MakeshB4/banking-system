package com.banking.useraccounts.entity;

import com.banking.useraccounts.enums.CustomerStatus;
import com.banking.useraccounts.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;


    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String alternatePhone;

    @Column(name = "pan_number", unique = true, length = 10)
    private String panNumber;

    @Column(name = "aadhar_number", unique = true, length = 12)
    private String aadharNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "country")
    private String country = "India";

    private String occupation;

    private Double annualIncome;

    private String nationality = "Indian";

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cif cif;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status = CustomerStatus.PENDING;
}