package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.dto.request.AddressRequest;
import com.banking.useraccounts.dto.request.KycRequest;
import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.entity.*;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
import com.banking.useraccounts.exceptions.UserRegistrationException;
import com.banking.useraccounts.repository.AddressRepository;
import com.banking.useraccounts.repository.CifRepository;
import com.banking.useraccounts.repository.CustomerRepository;
import com.banking.useraccounts.repository.KycDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private KycDetailsRepository kycDetailsRepository;

    @Mock
    private CifRepository cifRepository;

    @Mock
    private CifService cifService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    private UserRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new UserRegistrationRequest();
        request.setFirstName("Makesh");
        request.setLastName("Balasubramaniam");
        request.setEmail("makesh.b@example.com");
        request.setMobileNumber("1234567890");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("MALE");
        request.setNationality("Indian");
        request.setAnnualIncome(1000000.0);

        AddressRequest address = new AddressRequest();
        address.setAddressLine1("123 Street");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setPostalCode("560001");
        address.setAddressType("PERMANENT");
        request.setAddress(address);

        KycRequest kyc = new KycRequest();
        kyc.setIdProofType("AADHAAR");
        kyc.setIdProofNumber("123456789012");
        kyc.setAddressProofType("AADHAAR");
        kyc.setAddressProofNumber("123456789012");
        kyc.setPanNumber("ABCDE1234F");
        request.setKycDetails(kyc);

        AccountRequest account = new AccountRequest();
        account.setAccountType("SAVINGS");
        account.setInitialDeposit(new BigDecimal("5000"));
        request.setAccountDetails(account);
    }

    @Test
    void testRegisterUser_Success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Rajesh");
        customer.setLastName("Sharma");
        customer.setEmail("rajesh@example.com");
        customer.setStatus(Customer.CustomerStatus.PENDING);
        customer.setKycStatus(Customer.KycStatus.PENDING);

        Cif cif = new Cif();
        cif.setCifNumber("CIF20260421001");
        cif.setCifStatus(Cif.CifStatus.PENDING);

        Account account = new Account();
        account.setAccountNumber("ACC000100000001");
        account.setAccountType(Account.AccountType.SAVINGS);
        account.setStatus(Account.AccountStatus.PENDING);
        account.setCurrency("INR");

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(cifService.createCif(any(Customer.class))).thenReturn(cif);
        when(accountService.createAccount(any(Customer.class), any(AccountRequest.class))).thenReturn(account);

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        assertNotNull(response);
        assertEquals("CIF20260421001", response.getCifNumber());
        assertEquals("PENDING", response.getCustomerStatus());
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> {
            userRegistrationService.registerUser(request);
        });

        assertEquals("Email already registered: makesh.b@example.com", exception.getMessage());
    }

    @Test
    void testGetPendingCustomerByCifNumber_Success() {
        String cifNumber = "20260422458388";

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCifNumber("20260422458388");
        customer.setFirstName("Makesh");
        customer.setLastName("Balasubramaniam");
        customer.setEmail("Makesh.b@gmail.com");
        customer.setMobileNumber("1234567890");
        customer.setDateOfBirth(LocalDate.of(1985, 3, 15));
        customer.setStatus(Customer.CustomerStatus.PENDING);
        customer.setKycStatus(Customer.KycStatus.PENDING);
        customer.setCreationTime(LocalDateTime.of(2026, 4, 22, 21, 38, 51));

        when(customerRepository.findByCifNumber(cifNumber)).thenReturn(Optional.of(customer));

        PendingCustomerResponse response = userRegistrationService.getPendingCustomerById(cifNumber);

        assertNotNull(response);
        assertEquals("20260422458388", response.getCifNumber());
        assertEquals("Makesh Balasubramaniam", response.getFullName());
        assertEquals("Makesh.b@gmail.com", response.getEmail());
        assertEquals("PENDING", response.getCustomerStatus());
    }

    @Test
    void testGetPendingCustomerByCifNumber_NotFound() {
        String cifNumber = "99999999999999";

        when(customerRepository.findByCifNumber(cifNumber)).thenReturn(Optional.empty());

        DetailsNotFoundException exception = assertThrows(DetailsNotFoundException.class, () -> {
            userRegistrationService.getPendingCustomerById(cifNumber);
        });

        assertEquals("Customer not found with cifNumber: " + cifNumber, exception.getMessage());
    }

    @Test
    void testGetPendingCustomerByCifNumber_NotPending() {
        String cifNumber = "20260422458388";

        Customer customer = new Customer();
        customer.setCifNumber("20260422458388");
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        when(customerRepository.findByCifNumber(cifNumber)).thenReturn(Optional.of(customer));

        DetailsNotFoundException exception = assertThrows(DetailsNotFoundException.class, () -> {
            userRegistrationService.getPendingCustomerById(cifNumber);
        });

        assertEquals("Customer is not in pending status", exception.getMessage());
    }
}