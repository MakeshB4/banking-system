package com.banking.useraccounts.service;

import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;

public interface CifService {

    String generateCifNumber();

    Cif createCif(Customer customer);

    Cif getCifByCifNumber(String cifNumber);

    void activateCif(Long cifId, String approvedBy);

    void rejectCif(Long cifId, String remarks);
}