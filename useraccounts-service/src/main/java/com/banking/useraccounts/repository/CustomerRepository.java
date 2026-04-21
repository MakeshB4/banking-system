package com.banking.useraccounts.repository;

import com.banking.useraccounts.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Optional<Customer> findByCifNumber(String cifNumber);

    List<Customer> findByStatus(Customer.CustomerStatus status);

    List<Customer> findByKycStatus(Customer.KycStatus kycStatus);

    @Query("SELECT c FROM Customer c WHERE c.status = :status AND c.delFlg = false")
    List<Customer> findPendingCustomers(Customer.CustomerStatus status);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}