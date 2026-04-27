package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;
import com.banking.useraccounts.enums.CustomerStatus;
import com.banking.useraccounts.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer customer;
    private AccountRequest accountRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        accountRequest = new AccountRequest();
        accountRequest.setAccountType("SAVINGS");
        accountRequest.setInitialDeposit(new BigDecimal("5000"));
        accountRequest.setCurrency("INR");
        accountRequest.setBranchCode("BLR001");
    }

    @Test
    void testCreateAccount_Success() {
        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setAccountType("SAVINGS");
        savedAccount.setBalance(new BigDecimal("5000"));
        savedAccount.setCurrency("INR");
        savedAccount.setStatus("PENDING");

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Cif cif = new Cif();
        cif.setCustomerNumber(20260421001L);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        Account result = accountService.createAccount(accountRequest,cif);

        assertNotNull(result);
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(new BigDecimal("5000"), result.getBalance());
        assertEquals("INR", result.getCurrency());
        assertEquals("PENDING", result.getStatus());

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountRepository, times(1)).flush();
    }

    @Test
    void testCreateAccount_WithNullInitialDeposit() {
        accountRequest.setInitialDeposit(null);

        Account savedAccount = new Account();
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Cif cif = new Cif();
        cif.setCustomerNumber(20260421001L);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        Account result = accountService.createAccount(accountRequest,cif);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }
}