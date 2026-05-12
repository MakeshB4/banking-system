package com.banking.useraccounts.controller;

import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.exceptions.AccountNotFoundException;
import com.banking.useraccounts.service.AccountService;
import com.banking.useraccounts.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    private AccountResponse mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = createMockResponse();
        String dummyJwt = "dummy-jwt-token";
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user", dummyJwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnAccountBalanceWhenAccountExists() throws Exception {
        Long accountNumber = 123456L;

        when(accountService.getAccounts(accountNumber)).thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(get("/api/v1/accounts/getBalance/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        if (result.getResolvedException() != null) {
            result.getResolvedException().printStackTrace();
        }


        mockMvc.perform(get("/api/v1/accounts/getBalance/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(5000.00));
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        Long accountNumber = 123L;

        when(accountService.getAccounts(accountNumber))
                .thenThrow(new AccountNotFoundException("Account not found"));

        MvcResult result = mockMvc.perform(get("/api/v1/accounts/getBalance/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        Exception exception = result.getResolvedException();
        if (exception != null) {
            exception.printStackTrace();
        }

        mockMvc.perform(get("/api/v1/accounts/getBalance/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(accountService, times(2)).getAccounts(accountNumber);
    }

    private AccountResponse createMockResponse() {
        AccountResponse response = new AccountResponse();
        response.setId(1L);
        response.setBalance(BigDecimal.valueOf(5000.00));
        response.setAccountType("SAVINGS");
        response.setStatus("ACTIVE");
        return response;
    }
}