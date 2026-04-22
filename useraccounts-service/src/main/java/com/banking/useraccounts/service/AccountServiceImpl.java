package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Customer;
import com.banking.useraccounts.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    @Transactional
    public Account createAccount(Customer customer, AccountRequest accountRequest) {
        log.info("Creating account for customer: {}", customer.getEmail());

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(Account.AccountType.valueOf(accountRequest.getAccountType()));
        
        if(accountRequest.getInitialDeposit() != null) {
            account.setBalance(accountRequest.getInitialDeposit());
        } else {
            account.setBalance(BigDecimal.ZERO);
        }
        
        if(accountRequest.getCurrency() != null) {
            account.setCurrency(accountRequest.getCurrency());
        } else {
            account.setCurrency("INR");
        }
        
        account.setStatus(Account.AccountStatus.PENDING);
        
        if(accountRequest.getBranchCode() != null) {
            account.setBranchCode(accountRequest.getBranchCode());
        } else {
            account.setBranchCode("MAIN001");
        }
        
        account.setIfscCode("BANK0001234");
        account.setOpeningDate(LocalDate.now());
        

        account.setCreatedBy(customer.getEmail());

        Account savedAccount = accountRepository.save(account);
        accountRepository.flush();
        
        log.info("Account created successfully: {}", savedAccount.getAccountNumber());

        return savedAccount;
    }
}