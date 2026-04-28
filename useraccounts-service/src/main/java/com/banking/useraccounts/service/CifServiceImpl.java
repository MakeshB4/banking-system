package com.banking.useraccounts.service;

import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
import com.banking.useraccounts.repository.CifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class CifServiceImpl implements CifService {

    private final CifRepository cifRepository;
    private static final String CIF_PREFIX = "CIF";

    @Override
    public Long generateCustomerNumber() {
        LocalDate today = LocalDate.now();
        long datePrefix = (today.getYear() * 10000L) + (today.getMonthValue() * 100L) + today.getDayOfMonth();
        long count = cifRepository.count() + 1;

        Long customerNumber = (datePrefix * 100000L) + count;

        while (cifRepository.existsByCustomerNumber(customerNumber)) {
            count++;
            customerNumber = (datePrefix * 100000L) + count;
        }

        log.info("Generated customer number: {}", customerNumber);
        return customerNumber;
    }

    @Override
    @Transactional
    public Cif createCif(Customer customer) {
        log.info("Creating CIF for customer: {}", customer.getEmail());

        Cif cif = new Cif();
        cif.setCustomer(customer);
        cif.setCustomerType(Cif.CustomerType.INDIVIDUAL);
        cif.setCifStatus(Cif.CifStatus.PENDING);
        cif.setRiskCategory("LOW");
        cif.setCreatedBy(customer.getEmail());
        cif.setCustomerNumber(customer.getId());

        Cif savedCif = cifRepository.save(cif);
        log.info("CIF created successfully with number: {}", savedCif.getCustomerNumber());

        return savedCif;
    }

    @Override
    public Cif getCifByCustomerNumber(Long customerNumber) {
        System.out.println("cifNumber"+customerNumber);
        return cifRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with number: " + customerNumber));
    }

    @Override
    @Transactional
    public void activateCif(Long cifId, String approvedBy) {
        Cif cif = cifRepository.findById(cifId)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with ID: " + cifId));

        cif.setCifStatus(Cif.CifStatus.ACTIVE);
        cif.setActivationDate(LocalDate.now());
        cif.setApprovedBy(approvedBy);
        cif.setApprovedDate(LocalDate.now());
        cif.setUpdatedBy(approvedBy);

        cifRepository.save(cif);
        log.info("CIF activated: {}", cif.getCustomerNumber());
    }

    @Override
    @Transactional
    public void rejectCif(Long cifId, String remarks) {
        Cif cif = cifRepository.findById(cifId)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with ID: " + cifId));

        cif.setCifStatus(Cif.CifStatus.REJECTED);
        cif.setRemarks(remarks);

        cifRepository.save(cif);
        log.info("CIF rejected: {}", cif.getCustomerNumber());
    }
}