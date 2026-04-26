package com.banking.useraccounts.service;

import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;

public interface CifService {

    Long generateCustomerNumber();

    Cif createCif(Customer customer);

    Cif getCifByCustomerNumber(Long customerNumber);

    void activateCif(Long cifId, String approvedBy);

    void rejectCif(Long cifId, String remarks);
}