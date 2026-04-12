package com.cooperative.banking_system.service;

import com.cooperative.banking_system.dto.ContactUpdateDto;
import com.cooperative.banking_system.dto.CustomerRequestDto;
import com.cooperative.banking_system.dto.CustomerResponseDto;
import com.cooperative.banking_system.dto.KycUpdateDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto createCustomer(CustomerRequestDto request);

    CustomerResponseDto getCustomerByCif(String cifNumber);

    List<CustomerResponseDto> getAllCustomers();

    CustomerResponseDto updateCustomer(String cifNumber, CustomerRequestDto request);

    CustomerResponseDto updateContactInfo(String cifNumber, ContactUpdateDto request);

    CustomerResponseDto updateKycStatus(String cifNumber, KycUpdateDto request);

    void deleteCustomer(String cifNumber);
}
