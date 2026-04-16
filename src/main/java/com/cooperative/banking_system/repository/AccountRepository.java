package com.cooperative.banking_system.repository;

import com.cooperative.banking_system.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Accounts, Long> {

}
