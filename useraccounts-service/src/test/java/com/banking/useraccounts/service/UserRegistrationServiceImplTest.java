package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.*;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.entity.*;
import com.banking.useraccounts.enums.CustomerStatus;
import com.banking.useraccounts.enums.Gender;
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

    private UserModificationRequest userModificationRequest;

    @BeforeEach
    void setUp() {
        request = createValidUpdateRequest() ;
        userModificationRequest = createValidUpdateRequest();
    }

    @Test
    void testRegisterUser_Success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Rajesh");
        customer.setLastName("Sharma");
        customer.setEmail("rajesh@example.com");
        customer.setStatus(CustomerStatus.PENDING);

        Cif cif = new Cif();
        cif.setCustomerNumber(20260421001L);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        Account account = new Account();
        account.setAccountType("SAVINGS");
        account.setStatus("PENDING");
        account.setCurrency("INR");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(cifService.createCif(any(Customer.class))).thenReturn(cif);
        when(accountService.createAccount(any(AccountRequest.class), any(Cif.class))).thenReturn(account);

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        assertNotNull(response);
        assertEquals(20260421001L, response.getCustomerNumber());
        assertEquals("PENDING", response.getCustomerStatus());
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    void testGetPendingCustomerByCifNumber_Success() {
        Long customerNumber = 20260422458388L;

        Cif cif = new Cif();
        cif.setCustomerNumber(customerNumber);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCif(cif);
        customer.setFirstName("Makesh");
        customer.setLastName("Balasubramaniam");
        customer.setEmail("Makesh.b@gmail.com");
        customer.setPhoneNumber("1234567890");
        customer.setDateOfBirth(LocalDate.of(1985, 3, 15));
        customer.setStatus(CustomerStatus.PENDING);

        cif.setCustomer(customer);

        when(cifRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.of(cif));

        PendingCustomerResponse response = userRegistrationService.getPendingCustomerById(customerNumber);

        assertNotNull(response);
        assertEquals(20260422458388L, response.getCustomerNumber());
        assertEquals("Makesh Balasubramaniam", response.getFullName());
        assertEquals("Makesh.b@gmail.com", response.getEmail());
        assertEquals("PENDING", response.getCustomerStatus());
    }

    @Test
    void testGetPendingCustomerByCifNumber_NotFound() {
        Long customerNumber = 99999999999999L;


        when(cifRepository.findByCustomerNumber(customerNumber)).thenReturn(Optional.empty());

        DetailsNotFoundException exception = assertThrows(DetailsNotFoundException.class, () -> {
            userRegistrationService.getPendingCustomerById(customerNumber);
        });

        assertEquals("CIF not found with customerNumber: " + customerNumber, exception.getMessage());
    }

       @Test
    void testUpdateUser_CustomerNotFound() {

        DetailsNotFoundException exception = assertThrows(DetailsNotFoundException.class, () -> {
            userRegistrationService.updateUser(userModificationRequest);
        });

        assertEquals("CIF not found with customerNumber: 20260421001", exception.getMessage());
    }

    private UserModificationRequest createValidUpdateRequest() {
        UserModificationRequest request = new UserModificationRequest();
        request.setCustomerNumber(20260421001L);
        request.setStatus("APPROVE");
        request.setFirstName("Makesh");
        request.setMiddleName("");
        request.setLastName("Balasubramaniam");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setGender(Gender.MALE);
        request.setEmail("makesh.b@example.com");
        request.setMobileNumber("1234567890");
        request.setAlternateMobile("1234567890");
        request.setNationality("Indian");
        request.setOccupation("Software Engineer");
        request.setAnnualIncome(1200000.0);

        AddressRequest address = new AddressRequest();
        address.setAddressLine1("123 MG Road");
        address.setAddressLine2("Near City Mall");
        address.setLandmark("Opposite HDFC Bank");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setPostalCode("560001");
        address.setAddressType("PERMANENT");
        address.setIsCommunicationAddress(true);
        request.setAddress(address);

        KycRequest kycDetails = new KycRequest();
        kycDetails.setIdProofType("AADHAAR");
        kycDetails.setIdProofNumber("123456789012");
        kycDetails.setIdProofIssueDate(LocalDate.of(2015, 1, 10));
        kycDetails.setIdProofExpiryDate(LocalDate.of(2030, 1, 10));
        kycDetails.setAddressProofType("AADHAAR");
        kycDetails.setAddressProofNumber("123456789012");
        kycDetails.setPanNumber("ABCDE1234F");
        request.setKycDetails(kycDetails);

        AccountRequest accountDetails = new AccountRequest();
        accountDetails.setAccountType("SAVINGS");
        accountDetails.setInitialDeposit(new BigDecimal("5000.00"));
        accountDetails.setCurrency("INR");
        accountDetails.setBranchCode("BLR001");
        request.setAccountDetails(accountDetails);

        return request;
    }

    private UserRegistrationRequest createValidRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("Makesh");
        request.setMiddleName("");
        request.setLastName("Balasubramaniam");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setGender(Gender.MALE);
        request.setEmail("makesh.b@example.com");
        request.setMobileNumber("1234567890");
        request.setAlternateMobile("1234567890");
        request.setNationality("Indian");
        request.setOccupation("Software Engineer");
        request.setAnnualIncome(1200000.0);

        AddressRequest address = new AddressRequest();
        address.setAddressLine1("123 MG Road");
        address.setAddressLine2("Near City Mall");
        address.setLandmark("Opposite HDFC Bank");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setPostalCode("560001");
        address.setAddressType("PERMANENT");
        address.setIsCommunicationAddress(true);
        request.setAddress(address);

        KycRequest kycDetails = new KycRequest();
        kycDetails.setIdProofType("AADHAAR");
        kycDetails.setIdProofNumber("123456789012");
        kycDetails.setIdProofIssueDate(LocalDate.of(2015, 1, 10));
        kycDetails.setIdProofExpiryDate(LocalDate.of(2030, 1, 10));
        kycDetails.setAddressProofType("AADHAAR");
        kycDetails.setAddressProofNumber("123456789012");
        kycDetails.setPanNumber("ABCDE1234F");
        request.setKycDetails(kycDetails);

        AccountRequest accountDetails = new AccountRequest();
        accountDetails.setAccountType("SAVINGS");
        accountDetails.setInitialDeposit(new BigDecimal("5000.00"));
        accountDetails.setCurrency("INR");
        accountDetails.setBranchCode("BLR001");
        request.setAccountDetails(accountDetails);

        return request;
    }
}