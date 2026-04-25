package com.banking.useraccounts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    
    private Long id;
    private String accountNumber;
    private String accountType;
    private String cifNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
    private String branchCode;
    private String branchName;
    private LocalDateTime openedDate;
    private LocalDateTime closedDate;
    private String customerName;
    private String email;
    private String mobileNumber;
}