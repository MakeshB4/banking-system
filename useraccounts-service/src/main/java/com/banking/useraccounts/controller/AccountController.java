package com.banking.useraccounts.controller;

import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.service.AccountService;
import com.banking.useraccounts.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/getAllAccounts/{cifNumber}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCif(@PathVariable String cifNumber) {
        log.info("fetching All Accounts linked  cifNumber: {}", cifNumber);

        List<AccountResponse> accounts = accountService.fetchAllAccountByCif(cifNumber);

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}
