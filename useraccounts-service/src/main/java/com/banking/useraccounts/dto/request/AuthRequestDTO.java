package com.banking.useraccounts.dto.request;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}