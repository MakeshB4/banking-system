package com.banking.useraccounts.controller;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.dto.request.AddressRequest;
import com.banking.useraccounts.dto.request.KycRequest;
import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
import com.banking.useraccounts.exceptions.UserRegistrationException;
import com.banking.useraccounts.service.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRegistrationService userRegistrationService;

    private UserRegistrationRequest validRequest;
    private UserRegistrationResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = createValidRequest();
        mockResponse = createMockResponse();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userRegistrationService.registerUser(any(UserRegistrationRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registration successful. Pending admin approval."))
                .andExpect(jsonPath("$.cifNumber").value("CIF20260421001"))
                .andExpect(jsonPath("$.customerStatus").value("PENDING"))
                .andExpect(jsonPath("$.kycStatus").value("PENDING"))
                .andExpect(jsonPath("$.accountInfo.accountNumber").value("ACC000100000001"))
                .andExpect(jsonPath("$.customerInfo.email").value("makesh.b@example.com"));
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        when(userRegistrationService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new UserRegistrationException("Email already registered: " + validRequest.getEmail()));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Registration Error"))
                .andExpect(jsonPath("$.message").value("Email already registered: makesh.b@example.com"));
    }

    @Test
    void testGetPendingCustomerByCifNumber_Success() throws Exception {
        String cifNumber = "20260422458388";

        when(userRegistrationService.getPendingCustomerById(anyString())).thenReturn(getPendingCustomerResponse());

        mockMvc.perform(get("/api/v1/users/pending/" + cifNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cifNumber").value("20260422458388"))
                .andExpect(jsonPath("$.fullName").value("Makesh Balasubramaniam"))
                .andExpect(jsonPath("$.email").value("makesh.b@gmail.com"))
                .andExpect(jsonPath("$.customerStatus").value("PENDING"))
                .andExpect(jsonPath("$.address.city").value("Bangalore"))
                .andExpect(jsonPath("$.kycDetails.panNumber").value("ABCDE1234F"));
    }

    @Test
    void testGetPendingCustomerByCifNumber_NotFound() throws Exception {
        String cifNumber = "99999999999999";

        when(userRegistrationService.getPendingCustomerById(anyString()))
                .thenThrow(new DetailsNotFoundException("CIF not found: " + cifNumber));

        mockMvc.perform(get("/api/v1/users/pending/" + cifNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private UserRegistrationRequest createValidRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("Makesh");
        request.setMiddleName("");
        request.setLastName("Balasubramaniam");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setGender("MALE");
        request.setEmail("makesh.b@example.com");
        request.setMobileNumber("1234567890");
        request.setAlternateMobile("1234567890");
        request.setNationality("Indian");
        request.setMaritalStatus("Married");
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

    private UserRegistrationResponse createMockResponse() {
        UserRegistrationResponse.AccountInfo accountInfo = UserRegistrationResponse.AccountInfo.builder()
                .accountNumber("ACC000100000001")
                .accountType("SAVINGS")
                .accountStatus("PENDING")
                .currency("INR")
                .build();

        UserRegistrationResponse.CustomerInfo customerInfo = UserRegistrationResponse.CustomerInfo.builder()
                .customerId(1L)
                .fullName("Makesh Balasubramaniam")
                .email("makesh.b@example.com")
                .mobileNumber("9876543210")
                .build();

        return UserRegistrationResponse.builder()
                .message("User registration successful. Pending admin approval.")
                .cifNumber("CIF20260421001")
                .customerStatus("PENDING")
                .kycStatus("PENDING")
                .cifStatus("PENDING")
                .accountInfo(accountInfo)
                .customerInfo(customerInfo)
                .registrationTime(LocalDateTime.now())
                .build();
    }

    private PendingCustomerResponse getPendingCustomerResponse(){
        PendingCustomerResponse mockResponse = new PendingCustomerResponse();
        mockResponse.setCustomerId(1L);
        mockResponse.setCifNumber("20260422458388");
        mockResponse.setFullName("Makesh Balasubramaniam");
        mockResponse.setEmail("makesh.b@gmail.com");
        mockResponse.setMobileNumber("9999999999");
        mockResponse.setDateOfBirth(LocalDate.of(1985, 3, 15));
        mockResponse.setCustomerStatus("PENDING");
        mockResponse.setKycStatus("PENDING");
        mockResponse.setCifStatus("PENDING");

        PendingCustomerResponse.AddressInfo address = new PendingCustomerResponse.AddressInfo();
        address.setAddressLine1("G 305 ");
        address.setAddressLine2("Itina mahavir");
        address.setCity("Bangalore");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setPostalCode("517501");
        mockResponse.setAddress(address);

        PendingCustomerResponse.KycInfo kycInfo = new PendingCustomerResponse.KycInfo();
        kycInfo.setIdProofType("PASSPORT");
        kycInfo.setIdProofNumber("P1234567");
        kycInfo.setPanNumber("ABCDE1234F");
        kycInfo.setKycStatus("PENDING");
        mockResponse.setKycDetails(kycInfo);

        return  mockResponse;
    }

}