package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
import com.banking.useraccounts.repository.AccountRepository;
import com.banking.useraccounts.repository.CifRepository;
import com.banking.useraccounts.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    private final CifRepository cifRepository;



    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, CifRepository cifRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cifRepository = cifRepository;
    }

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

    @Transactional(readOnly = true)
    public List<AccountResponse> fetchAllAccountByCif(String cifNumber) {
        Customer customer = customerRepository.findByCifNumber(cifNumber)
                .orElseThrow(() -> new DetailsNotFoundException("Customer not found with cifNumber: " + cifNumber));

        List<Account> accounts = accountRepository.findByCustomerId(customer.getId());

        return accounts.stream()
                .map(account -> mapToAccountResponse(account, customer))
                .collect(Collectors.toList());
    }

    private AccountResponse mapToAccountResponse(Account account, Customer customer) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType() != null ? account.getAccountType().name() : null)
                .cifNumber(customer.getCifNumber())  // Get from Customer, not Account
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus() != null ? account.getStatus().name() : null)
                .branchCode(account.getBranchCode())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .mobileNumber(customer.getMobileNumber())
                .build();
    }
}