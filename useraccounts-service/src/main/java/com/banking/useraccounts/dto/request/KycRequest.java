package com.banking.useraccounts.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycRequest {

    @NotBlank(message = "ID proof type is required")
    @Pattern(regexp = "AADHAAR|PASSPORT|DRIVING_LICENSE|VOTER_ID|NATIONAL_ID", 
             message = "Invalid ID proof type")
    private String idProofType;

    @NotBlank(message = "ID proof number is required")
    @Size(max = 50, message = "ID proof number cannot exceed 50 characters")
    private String idProofNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idProofIssueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate idProofExpiryDate;

    private String idProofDocumentPath;

    @NotBlank(message = "Address proof type is required")
    @Pattern(regexp = "AADHAAR|PASSPORT|UTILITY_BILL|BANK_STATEMENT|RENTAL_AGREEMENT|VOTER_ID", 
             message = "Invalid address proof type")
    private String addressProofType;

    @NotBlank(message = "Address proof number is required")
    private String addressProofNumber;

    private String addressProofDocumentPath;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    private String panDocumentPath;

    private String photoPath;

    private String signaturePath;
}
