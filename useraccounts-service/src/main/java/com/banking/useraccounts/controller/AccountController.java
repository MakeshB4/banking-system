package com.banking.useraccounts.controller;

import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/getAllAccounts/{customerNumber}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCif(@PathVariable Long customerNumber) {
        log.info("fetching All Accounts linked  cifNumber: {}", customerNumber);

        List<AccountResponse> accounts = accountService.fetchAllAccountByCif(customerNumber);

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/getBalance/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountBalance(@PathVariable Long accountNumber) throws AccountNotFoundException {
        log.info("fetching balance for account {}", accountNumber);

        AccountResponse accountResponse = accountService.getAccounts(accountNumber);

        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }

}
