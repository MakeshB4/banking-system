package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.dto.response.AccountResponse;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Customer;

import java.util.List;

public interface AccountService {

    Account createAccount(Customer customer, AccountRequest accountRequest);

    List<AccountResponse> fetchAllAccountByCif(String cifNumber);

}