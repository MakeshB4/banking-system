package com.cooperative.banking_system.dto;

import java.time.LocalDateTime;

public record AccountDTO(long accountNumber, String accountName, String accountType, Double balance,
                         LocalDateTime accountOpenDate) {
}
