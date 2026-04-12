package com.cooperative.banking_system.dto;

import com.cooperative.banking_system.entity.Customer.KycStatus;
import jakarta.validation.constraints.NotNull;

public class KycUpdateDto {

    @NotNull(message = "KYC status is required")
    private KycStatus kycStatus;

    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
}
