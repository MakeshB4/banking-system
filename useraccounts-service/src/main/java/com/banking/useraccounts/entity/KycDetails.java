package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;



@Entity
@Table(name = "kyc_details")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KycDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private IdProofType idProofType;

    private String idProofNumber;

    private LocalDate idProofIssueDate;

    private LocalDate idProofExpiryDate;

    private String idProofDocumentPath;

    @Enumerated(EnumType.STRING)
    private AddressProofType addressProofType;

    private String addressProofNumber;

    private String addressProofDocumentPath;

    private String panNumber;

    private String panDocumentPath;

    private String photoPath;

    private String signaturePath;

    private String verifiedBy;

    private LocalDate verifiedDate;

    private String kycRemarks;

    public enum IdProofType {
        AADHAAR,
        PASSPORT,
        DRIVING_LICENSE,
        VOTER_ID,
        NATIONAL_ID
    }

    public enum AddressProofType {
        AADHAAR,
        PASSPORT,
        UTILITY_BILL,
        BANK_STATEMENT,
        RENTAL_AGREEMENT,
        VOTER_ID
    }
}
