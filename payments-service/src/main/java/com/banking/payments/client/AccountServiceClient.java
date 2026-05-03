package com.banking.payments.client;

import com.banking.payments.dto.AccountBalanceResponse;
import com.banking.payments.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceClient {

    private final RestTemplate restTemplate;

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081/useraccounts-service/api/v1/accounts";

    public BigDecimal getAccountBalance(Long accountNumber, String jwtToken) {
        AccountBalanceResponse response;
        try {
            String url = ACCOUNT_SERVICE_URL + "/getBalance/" + accountNumber;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);


            System.out.println("Authorization header: " + jwtToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AccountBalanceResponse> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AccountBalanceResponse.class
            );

            response = resp.getBody();

            if (response != null) {
                return response.getBalance();
            }

        } catch (Exception exception) {
            throw new AccountNotFoundException("Error fetching balance for account: " + accountNumber + " " + exception.getMessage());
        }
        return null;
    }

    public AccountBalanceResponse getAccountDetails(String accountNumber, String jwtToken) {
        try {
            String url = ACCOUNT_SERVICE_URL + "/getBalance/" + accountNumber;

            // Set Authorization header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AccountBalanceResponse> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AccountBalanceResponse.class
            );

            return resp.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching account details: " + accountNumber, e);
        }
    }
}