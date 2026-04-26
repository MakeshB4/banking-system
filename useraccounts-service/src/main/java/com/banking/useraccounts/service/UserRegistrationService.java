package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.UserModificationRequest;
import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;


public interface UserRegistrationService {


    UserRegistrationResponse registerUser(UserRegistrationRequest request);

    PendingCustomerResponse getPendingCustomerById(Long customerNumber);

    UserRegistrationResponse updateUser(UserModificationRequest request);
}