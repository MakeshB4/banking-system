package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.entity.*;
import com.banking.useraccounts.exceptions.UserRegistrationException;
import com.banking.useraccounts.repository.AddressRepository;
import com.banking.useraccounts.repository.CifRepository;
import com.banking.useraccounts.repository.CustomerRepository;
import com.banking.useraccounts.repository.KycDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final KycDetailsRepository kycDetailsRepository;
    private final CifRepository cifRepository;
    private final CifService cifService;
    private final AccountService accountService;

    @Override
    @Transactional
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        log.info("Starting user registration for email: {}", request.getEmail());

        // Validation
        validateRegistrationRequest(request);

        // Create Customer
        Customer customer = createCustomer(request);
        Customer savedCustomer = customerRepository.save(customer);

        // Create Address
        Address address = createAddress(savedCustomer, request);
        addressRepository.save(address);

        // Create KYC Details
        KycDetails kycDetails = createKycDetails(savedCustomer, request);
        kycDetailsRepository.save(kycDetails);

        // Create CIF
        Cif cif = cifService.createCif(savedCustomer);

        // Update customer with CIF number
        savedCustomer.setCifNumber(cif.getCifNumber());
        customerRepository.save(savedCustomer);

        // Create Account
        Account account = accountService.createAccount(savedCustomer, request.getAccountDetails());

        log.info("User registration completed successfully for: {}", request.getEmail());

        return buildRegistrationResponse(savedCustomer, cif, account);
    }

    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new UserRegistrationException("Email already registered: " + request.getEmail());
        }

        if (customerRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserRegistrationException("Mobile number already registered: " + request.getMobileNumber());
        }

        if (request.getKycDetails().getIdProofNumber() != null &&
                kycDetailsRepository.existsByIdProofNumber(request.getKycDetails().getIdProofNumber())) {
            throw new UserRegistrationException("ID Proof number already registered");
        }

        if (request.getKycDetails().getPanNumber() != null &&
                kycDetailsRepository.existsByPanNumber(request.getKycDetails().getPanNumber())) {
            throw new UserRegistrationException("PAN number already registered");
        }
    }


    private Customer createCustomer(UserRegistrationRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setAlternateMobile(request.getAlternateMobile());
        customer.setNationality(request.getNationality());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setStatus(Customer.CustomerStatus.PENDING);
        customer.setKycStatus(Customer.KycStatus.PENDING);
        customer.setCreatedBy(request.getEmail());
        return customer;
    }

    private Address createAddress(Customer customer, UserRegistrationRequest request) {
        Address address = new Address();
        address.setCustomer(customer);
        address.setAddressLine1(request.getAddress().getAddressLine1());
        address.setAddressLine2(request.getAddress().getAddressLine2());
        address.setLandmark(request.getAddress().getLandmark());
        address.setCity(request.getAddress().getCity());
        address.setState(request.getAddress().getState());
        address.setCountry(request.getAddress().getCountry());
        address.setPostalCode(request.getAddress().getPostalCode());
        address.setAddressType(Address.AddressType.valueOf(request.getAddress().getAddressType()));
        address.setIsCommunicationAddress(request.getAddress().getIsCommunicationAddress());
        address.setCreatedBy(customer.getEmail());
        return address;
    }

    private KycDetails createKycDetails(Customer customer, UserRegistrationRequest request) {
        KycDetails kycDetails = new KycDetails();
        kycDetails.setCustomer(customer);
        kycDetails.setIdProofType(KycDetails.IdProofType.valueOf(request.getKycDetails().getIdProofType()));
        kycDetails.setIdProofNumber(request.getKycDetails().getIdProofNumber());
        kycDetails.setIdProofIssueDate(request.getKycDetails().getIdProofIssueDate());
        kycDetails.setIdProofExpiryDate(request.getKycDetails().getIdProofExpiryDate());
        kycDetails.setIdProofDocumentPath(request.getKycDetails().getIdProofDocumentPath());
        kycDetails.setAddressProofType(KycDetails.AddressProofType.valueOf(request.getKycDetails().getAddressProofType()));
        kycDetails.setAddressProofNumber(request.getKycDetails().getAddressProofNumber());
        kycDetails.setAddressProofDocumentPath(request.getKycDetails().getAddressProofDocumentPath());
        kycDetails.setPanNumber(request.getKycDetails().getPanNumber());
        kycDetails.setPanDocumentPath(request.getKycDetails().getPanDocumentPath());
        kycDetails.setPhotoPath(request.getKycDetails().getPhotoPath());
        kycDetails.setSignaturePath(request.getKycDetails().getSignaturePath());
        kycDetails.setCreatedBy(customer.getEmail());
        return kycDetails;
    }

    private UserRegistrationResponse buildRegistrationResponse(Customer customer, Cif cif, Account account) {
        return UserRegistrationResponse.builder()
                .message("User registration successful. Pending admin approval.")
                .cifNumber(cif.getCifNumber())
                .customerStatus(customer.getStatus().name())
                .kycStatus(customer.getKycStatus().name())
                .cifStatus(cif.getCifStatus().name())
                .accountInfo(UserRegistrationResponse.AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType().name())
                        .accountStatus(account.getStatus().name())
                        .currency(account.getCurrency())
                        .build())
                .customerInfo(UserRegistrationResponse.CustomerInfo.builder()
                        .customerId(customer.getId())
                        .fullName(customer.getFirstName() + " " + customer.getLastName())
                        .email(customer.getEmail())
                        .mobileNumber(customer.getMobileNumber())
                        .build())
                .registrationTime(LocalDateTime.now())
                .build();
    }


}
