package com.cooperative.banking_system.controller;

import com.cooperative.banking_system.dto.CustomerResponseDto;
import com.cooperative.banking_system.dto.KycUpdateDto;
import com.cooperative.banking_system.entity.Customer.KycStatus;
import com.cooperative.banking_system.exception.DuplicateResourceException;
import com.cooperative.banking_system.exception.GlobalExceptionHandler;
import com.cooperative.banking_system.exception.ResourceNotFoundException;
import com.cooperative.banking_system.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Adding Comments for Testing
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private CustomerResponseDto responseDto;

    private static final String VALID_CREATE_REQUEST = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john.doe@example.com",
              "phoneNumber": "+1234567890",
              "dateOfBirth": "1990-01-15",
              "gender": "MALE",
              "address": "123 Main St"
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDto = new CustomerResponseDto();
        responseDto.setId(1L);
        responseDto.setCifNumber("CIF123456789");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setEmail("john.doe@example.com");
        responseDto.setPhoneNumber("+1234567890");
        responseDto.setDateOfBirth(LocalDate.of(1990, 1, 15));
        responseDto.setGender("MALE");
        responseDto.setAddress("123 Main St");
        responseDto.setKycStatus(KycStatus.PENDING);
        responseDto.setCreatedAt(LocalDateTime.now());
        responseDto.setUpdatedAt(LocalDateTime.now());
    }

    // --- POST /api/v1/customers ---

    @Test
    void createCustomer_validRequest_returns201() throws Exception {
        when(customerService.createCustomer(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.cifNumber").value("CIF123456789"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void createCustomer_missingFirstName_returns400() throws Exception {
        String body = """
                {
                  "lastName": "Doe",
                  "email": "john.doe@example.com",
                  "phoneNumber": "+1234567890",
                  "dateOfBirth": "1990-01-15"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void createCustomer_invalidEmail_returns400() throws Exception {
        String body = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "not-an-email",
                  "phoneNumber": "+1234567890",
                  "dateOfBirth": "1990-01-15"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_duplicateEmail_returns409() throws Exception {
        when(customerService.createCustomer(any()))
                .thenThrow(new DuplicateResourceException("Customer with email already exists"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // --- GET /api/v1/customers/{cifNumber} ---

    @Test
    void getCustomerByCif_exists_returns200() throws Exception {
        when(customerService.getCustomerByCif("CIF123456789")).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/customers/CIF123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.cifNumber").value("CIF123456789"));
    }

    @Test
    void getCustomerByCif_notFound_returns404() throws Exception {
        when(customerService.getCustomerByCif("INVALID"))
                .thenThrow(new ResourceNotFoundException("Customer not found with CIF: INVALID"));

        mockMvc.perform(get("/api/v1/customers/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    // --- GET /api/v1/customers ---

    @Test
    void getAllCustomers_returnsListWith200() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getAllCustomers_empty_returnsEmptyList() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // --- PUT /api/v1/customers/{cifNumber} ---

    @Test
    void updateCustomer_validRequest_returns200() throws Exception {
        when(customerService.updateCustomer(eq("CIF123456789"), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/customers/CIF123456789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void updateCustomer_notFound_returns404() throws Exception {
        when(customerService.updateCustomer(eq("INVALID"), any()))
                .thenThrow(new ResourceNotFoundException("Customer not found with CIF: INVALID"));

        mockMvc.perform(put("/api/v1/customers/INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_REQUEST))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/v1/customers/{cifNumber}/kyc ---

    @Test
    void updateKycStatus_toVerified_returns200() throws Exception {
        responseDto.setKycStatus(KycStatus.VERIFIED);
        when(customerService.updateKycStatus(eq("CIF123456789"), any(KycUpdateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/customers/CIF123456789/kyc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kycStatus\": \"VERIFIED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.kycStatus").value("VERIFIED"));
    }

    // --- DELETE /api/v1/customers/{cifNumber} ---

    @Test
    void deleteCustomer_exists_returns200() throws Exception {
        doNothing().when(customerService).deleteCustomer("CIF123456789");

        mockMvc.perform(delete("/api/v1/customers/CIF123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
    }

    @Test
    void deleteCustomer_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Customer not found with CIF: INVALID"))
                .when(customerService).deleteCustomer("INVALID");

        mockMvc.perform(delete("/api/v1/customers/INVALID"))
                .andExpect(status().isNotFound());
    }
}
