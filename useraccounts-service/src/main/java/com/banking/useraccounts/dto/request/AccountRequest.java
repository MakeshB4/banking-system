package com.banking.useraccounts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT",
             message = "Invalid account type")
    private String accountType;

    @Positive(message = "Initial deposit must be positive")
    private BigDecimal initialDeposit = BigDecimal.ZERO;

    @Pattern(regexp = "INR", message = "Currency must be INR")
    private String currency = "INR";

    private String accountHolderName;

    private String branchCode;

    private Long id;

    private String nomineeName;

    private String nomineeRelation;

    private String nomineeMobile;
}
