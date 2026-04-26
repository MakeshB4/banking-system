package com.banking.useraccounts.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCustomerResponse {

    private Long cifID;
    private Long customerNumber;
    private String fullName;
    private String email;
    private String mobileNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private String customerStatus;
    private String kycStatus;
    private String cifStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationTime;
    
    private AddressInfo address;
    private KycInfo kycDetails;
    private List<AccountInfo> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfo {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String country;
        private String postalCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KycInfo {
        private String idProofType;
        private String idProofNumber;
        private String panNumber;
        private String kycStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountInfo {
        private String accountNumber;
        private String accountType;
        private String accountStatus;
    }
}