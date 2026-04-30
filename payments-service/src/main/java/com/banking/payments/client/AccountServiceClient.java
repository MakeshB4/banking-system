package com.banking.payments.client;

import com.banking.payments.dto.AccountBalanceResponse;
import com.banking.payments.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceClient {
    
    private final RestTemplate restTemplate;
    
    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081/useraccounts-service/api/v1/accounts";
    
    public BigDecimal getAccountBalance(Long accountNumber) {
        AccountBalanceResponse response;
        try {
            String url = ACCOUNT_SERVICE_URL + "/getBalance/" + accountNumber;
             response = restTemplate.getForObject(url, AccountBalanceResponse.class);

            if (response != null) {
                return response.getBalance();
            }
            // throw new RuntimeException("Failed to fetch account balance");

        } catch (Exception exception) {
            throw new AccountNotFoundException("Error fetching balance for account: " + accountNumber + " " + exception.getMessage());
        }
     return response.getBalance();
    }
    
    public AccountBalanceResponse getAccountDetails(String accountNumber) {
        try {
            String url = ACCOUNT_SERVICE_URL + "/getBalance/" + accountNumber;
            return restTemplate.getForObject(url, AccountBalanceResponse.class);
            
        } catch (Exception e) {
            throw new RuntimeException("Error fetching account details: " + accountNumber, e);
        }
    }
}