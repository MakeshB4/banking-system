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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public Account createAccount(AccountRequest accountRequest,Cif cif) {
        log.info("Creating account for cif: {}", cif.getId());

        Account account = new Account();
        account.setCif(cif);
        account.setAccountType(accountRequest.getAccountType());
        
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
        
        account.setStatus("PENDING");
        
        if(accountRequest.getBranchCode() != null) {
            account.setBranchCode(accountRequest.getBranchCode());
        } else {
            account.setBranchCode("MAIN001");
        }
        
        account.setIfscCode("BANK0001234");
        account.setOpeningDate(LocalDate.now());
        

        account.setCreatedBy(cif.getCreatedBy());

        Account savedAccount = accountRepository.save(account);
        accountRepository.flush();
        
        log.info("Account created successfully: {}", savedAccount.getId());

        return savedAccount;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> fetchAllAccountByCif(Long customerNumber) {
        Customer customer = customerRepository.findById(customerNumber)
                .orElseThrow(() -> new DetailsNotFoundException("Customer not found with customerNumber: " + customerNumber));

        List<Account> accounts = accountRepository.findByCifId(customer.getCif().getId());

        return accounts.stream()
                .map(account -> mapToAccountResponse(account, customer))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccounts(Long account) throws AccountNotFoundException {
        Account accounts = accountRepository.findById(account)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return  mapToAccountResponse(accounts);

    }

    private AccountResponse mapToAccountResponse(Account account, Customer customer) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountType(account.getAccountType() != null ? account.getAccountType() : null)
                .customerNumber(account.getCif().getId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus() != null ? account.getStatus() : null)
                .branchCode(account.getBranchCode())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .mobileNumber(customer.getPhoneNumber())
                .build();
    }

    public AccountResponse mapToAccountResponse(Account account) throws AccountNotFoundException {
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setBalance(account.getBalance());
        return response;
    }
}