package com.cooperative.banking_system.service.impl;

import com.cooperative.banking_system.dto.ContactUpdateDto;
import com.cooperative.banking_system.dto.CustomerRequestDto;
import com.cooperative.banking_system.dto.CustomerResponseDto;
import com.cooperative.banking_system.dto.KycUpdateDto;
import com.cooperative.banking_system.entity.Customer;
import com.cooperative.banking_system.exception.DuplicateResourceException;
import com.cooperative.banking_system.exception.ResourceNotFoundException;
import com.cooperative.banking_system.repository.CustomerRepository;
import com.cooperative.banking_system.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + request.getEmail() + "' already exists");
        }

        Customer customer = new Customer();
        customer.setCifNumber(generateCifNumber());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setAddress(request.getAddress());
        customer.setKycStatus(Customer.KycStatus.PENDING);

        Customer saved = customerRepository.save(customer);
        logger.info("Customer created with CIF: {}", saved.getCifNumber());
        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerByCif(String cifNumber) {
        Customer customer = findByCifOrThrow(cifNumber);
        return toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findByDelFlag("N").stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public CustomerResponseDto updateCustomer(String cifNumber, CustomerRequestDto request) {
        Customer customer = findByCifOrThrow(cifNumber);

        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use");
        }

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setAddress(request.getAddress());

        Customer updated = customerRepository.save(customer);
        logger.info("Customer updated with CIF: {}", cifNumber);
        return toResponseDto(updated);
    }

    @Override
    public CustomerResponseDto updateContactInfo(String cifNumber, ContactUpdateDto request) {
        Customer customer = findByCifOrThrow(cifNumber);

        if (request.getEmail() != null) {
            if (!customer.getEmail().equals(request.getEmail())
                    && customerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use");
            }
            customer.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        Customer updated = customerRepository.save(customer);
        logger.info("Contact info updated for CIF: {}", cifNumber);
        return toResponseDto(updated);
    }

    @Override
    public CustomerResponseDto updateKycStatus(String cifNumber, KycUpdateDto request) {
        Customer customer = findByCifOrThrow(cifNumber);
        customer.setKycStatus(request.getKycStatus());
        Customer updated = customerRepository.save(customer);
        logger.info("KYC status updated to {} for CIF: {}", request.getKycStatus(), cifNumber);
        return toResponseDto(updated);
    }

    @Override
    public void deleteCustomer(String cifNumber) {
        Customer customer = findByCifOrThrow(cifNumber);
        customer.setDelFlag("Y");
        customerRepository.save(customer);
        logger.info("Customer soft-deleted with CIF: {}", cifNumber);
    }

    private Customer findByCifOrThrow(String cifNumber) {
        return customerRepository.findByCifNumberAndDelFlag(cifNumber, "N")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with CIF: " + cifNumber));
    }

    private String generateCifNumber() {
        String cif;
        do {
            cif = "CIF" + UUID.randomUUID().toString().replace("-", "").substring(0, 9).toUpperCase();
        } while (customerRepository.existsByCifNumber(cif));
        return cif;
    }

    private CustomerResponseDto toResponseDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setCifNumber(customer.getCifNumber());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setGender(customer.getGender());
        dto.setAddress(customer.getAddress());
        dto.setKycStatus(customer.getKycStatus());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        dto.setDelFlag(customer.getDelFlag());
        return dto;
    }
}
