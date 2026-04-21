package com.banking.useraccounts.repository;

import com.banking.useraccounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByStatus(Account.AccountStatus status);

    List<Account> findByCustomerIdAndStatus(Long customerId, Account.AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);
}
