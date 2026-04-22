package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.AccountRequest;
import com.banking.useraccounts.entity.Account;
import com.banking.useraccounts.entity.Customer;

public interface AccountService {

    Account createAccount(Customer customer, AccountRequest accountRequest);


}