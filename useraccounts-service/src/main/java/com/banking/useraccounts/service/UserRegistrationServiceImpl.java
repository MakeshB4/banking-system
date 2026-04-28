package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.request.UserModificationRequest;
import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.entity.*;
import com.banking.useraccounts.enums.CustomerStatus;
import com.banking.useraccounts.enums.Gender;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
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

        // Create CIF and link to customer
        Cif cif = cifService.createCif(savedCustomer);

        // Update customer with CIF reference
        savedCustomer.setCif(cif);
        customerRepository.save(savedCustomer);

        request.getAccountDetails().setAccountHolderName(savedCustomer.getFirstName());

        // Create Account linked to CIF
        Account account = accountService.createAccount(request.getAccountDetails(),cif);

        log.info("User registration completed successfully for: {}", request.getEmail());

        return buildRegistrationResponse(savedCustomer, cif, account);
    }

    @Override
    @Transactional(readOnly = true)
    public PendingCustomerResponse getPendingCustomerById(Long customerNumber) {
        log.info("Fetching pending customer with customerNumber: {}", customerNumber);

        // Find CIF by customer number
        Cif cif = cifRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with customerNumber: " + customerNumber));

        Customer customer = cif.getCustomer();

        if (customer == null) {
            throw new DetailsNotFoundException("Customer not found");
        }

        if (!CustomerStatus.PENDING.equals(customer.getStatus())) {
            throw new DetailsNotFoundException("Customer is not in pending status");
        }

        return buildPendingCustomerResponse(customer, cif);
    }

    @Transactional
    public UserRegistrationResponse updateUser(UserModificationRequest request) {
        log.info("Updating user with Customer Number: {}", request.getCustomerNumber());

        // Find CIF by customer number
        Cif cif = cifRepository.findByCustomerNumber(request.getCustomerNumber())
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with customerNumber: " + request.getCustomerNumber()));

        Customer customer = cif.getCustomer();

        if (!CustomerStatus.PENDING.equals(customer.getStatus())) {
            throw new DetailsNotFoundException("Customer is not in pending status");
        }

        updateCustomer(customer, request);
        customerRepository.save(customer);

        Address address = addressRepository.findByCustomerId(customer.getId())
                .orElse(new Address());
        updateAddress(address, customer, request);
        addressRepository.save(address);

        KycDetails kycDetails = kycDetailsRepository.findByCustomerId(customer.getId())
                .orElse(new KycDetails());
        updateKycDetails(kycDetails, customer, request);
        kycDetailsRepository.save(kycDetails);

        if ("APPROVE".equalsIgnoreCase(request.getStatus())) {
            cif.setCifStatus(Cif.CifStatus.ACTIVE);
            customer.setStatus(CustomerStatus.ACTIVE);
        } else if ("REJECT".equalsIgnoreCase(request.getStatus())) {
            cif.setCifStatus(Cif.CifStatus.CLOSED);
            customer.setStatus(CustomerStatus.REJECTED);
        }

        cifRepository.save(cif);

        return UserRegistrationResponse.builder()
                .customerNumber(cif.getCustomerNumber())
                .message("User updated successfully")
                .registrationTime(LocalDateTime.now())
                .build();
    }

    private void validateRegistrationRequest(UserRegistrationRequest request) {
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
        customer.setGender(Gender.MALE);
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getMobileNumber());
        customer.setAlternatePhone(request.getAlternateMobile());
        customer.setNationality(request.getNationality());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
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
        return kycDetails;
    }

    private UserRegistrationResponse buildRegistrationResponse(Customer customer, Cif cif, Account account) {
        return UserRegistrationResponse.builder()
                .message("User registration successful. Pending admin approval.")
                .customerNumber(cif.getCustomerNumber())
                .customerStatus(customer.getStatus().name())
                .accountInfo(UserRegistrationResponse.AccountInfo.builder()
                        .accountType(account.getAccountType())
                        .accountStatus(account.getStatus())
                        .currency(account.getCurrency())
                        .build())
                .customerInfo(UserRegistrationResponse.CustomerInfo.builder()
                        .customerId(customer.getId())
                        .fullName(customer.getFirstName() + " " + customer.getLastName())
                        .email(customer.getEmail())
                        .mobileNumber(customer.getPhoneNumber())
                        .build())
                .registrationTime(LocalDateTime.now())
                .build();
    }

    private PendingCustomerResponse buildPendingCustomerResponse(Customer customer, Cif cif) {
        Address address = addressRepository.findByCustomerId(customer.getId()).orElse(null);
        KycDetails kycDetails = kycDetailsRepository.findByCustomerId(customer.getId()).orElse(null);

        PendingCustomerResponse response = new PendingCustomerResponse();
        response.setCifID(customer.getId());
        response.setCustomerNumber(cif.getCustomerNumber());
        response.setFullName(customer.getFirstName() + " " + customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setMobileNumber(customer.getPhoneNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setCifStatus(cif.getCifStatus().name());
        response.setRegistrationTime(customer.getCreatedAt());
        response.setCustomerStatus(cif.getCifStatus().name());

        if (address != null) {
            PendingCustomerResponse.AddressInfo addressInfo = new PendingCustomerResponse.AddressInfo();
            addressInfo.setAddressLine1(address.getAddressLine1());
            addressInfo.setAddressLine2(address.getAddressLine2());
            addressInfo.setCity(address.getCity());
            addressInfo.setState(address.getState());
            addressInfo.setCountry(address.getCountry());
            addressInfo.setPostalCode(address.getPostalCode());
            response.setAddress(addressInfo);
        }

        if (kycDetails != null) {
            PendingCustomerResponse.KycInfo kycInfo = new PendingCustomerResponse.KycInfo();
            kycInfo.setIdProofType(kycDetails.getIdProofType().name());
            kycInfo.setIdProofNumber(kycDetails.getIdProofNumber());
            kycInfo.setPanNumber(kycDetails.getPanNumber());
            response.setKycDetails(kycInfo);
        }

        return response;
    }

    private void updateCustomer(Customer customer, UserModificationRequest request) {
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getMobileNumber());
        customer.setAlternatePhone(request.getAlternateMobile());
        customer.setNationality(request.getNationality());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
    }

    private void updateAddress(Address address, Customer customer, UserModificationRequest request) {
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
    }

    private void updateKycDetails(KycDetails kycDetails, Customer customer, UserModificationRequest request) {
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
    }
}