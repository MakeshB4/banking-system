package com.cooperative.banking_system.repository;

import com.cooperative.banking_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCifNumber(String cifNumber);

    Optional<Customer> findByCifNumberAndDelFlag(String cifNumber, String delFlag);

    List<Customer> findByDelFlag(String delFlag);

    boolean existsByEmail(String email);

    boolean existsByCifNumber(String cifNumber);
}
