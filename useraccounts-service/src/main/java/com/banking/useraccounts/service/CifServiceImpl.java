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
    public String generateCifNumber() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomNum = String.format("%06d", new Random().nextInt(999999));
        String cifNumber = timestamp + randomNum;

        // Ensure uniqueness
        while (cifRepository.existsByCifNumber(cifNumber)) {
            randomNum = String.format("%06d", new Random().nextInt(999999));
            cifNumber = timestamp + randomNum;
        }

        log.info("Generated CIF number: {}", cifNumber);
        return cifNumber;
    }

    @Override
    @Transactional
    public Cif createCif(Customer customer) {
        log.info("Creating CIF for customer: {}", customer.getEmail());

        Cif cif = new Cif();
        cif.setCifNumber(generateCifNumber());
        cif.setCustomer(customer);
        cif.setCustomerType(Cif.CustomerType.INDIVIDUAL);
        cif.setCifStatus(Cif.CifStatus.PENDING);
        cif.setRiskCategory("LOW");
        cif.setCreatedBy(customer.getEmail());

        Cif savedCif = cifRepository.save(cif);
        log.info("CIF created successfully with number: {}", savedCif.getCifNumber());

        return savedCif;
    }

    @Override
    public Cif getCifByCifNumber(String cifNumber) {
        System.out.println("cifNumber"+cifNumber);
        return cifRepository.findByCifNumber(cifNumber)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with number: " + cifNumber));
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
        cif.setModifiedBy(approvedBy);

        cifRepository.save(cif);
        log.info("CIF activated: {}", cif.getCifNumber());
    }

    @Override
    @Transactional
    public void rejectCif(Long cifId, String remarks) {
        Cif cif = cifRepository.findById(cifId)
                .orElseThrow(() -> new DetailsNotFoundException("CIF not found with ID: " + cifId));

        cif.setCifStatus(Cif.CifStatus.REJECTED);
        cif.setRemarks(remarks);

        cifRepository.save(cif);
        log.info("CIF rejected: {}", cif.getCifNumber());
    }
}