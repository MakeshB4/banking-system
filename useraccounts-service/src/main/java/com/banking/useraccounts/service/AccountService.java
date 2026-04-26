package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Customer;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface AccountService {

    Account createAccount(Customer customer, AccountRequest accountRequest);

    List<AccountResponse> fetchAllAccountByCif(String cifNumber);

    AccountResponse getAccounts(String accountNumber) throws AccountNotFoundException;

}