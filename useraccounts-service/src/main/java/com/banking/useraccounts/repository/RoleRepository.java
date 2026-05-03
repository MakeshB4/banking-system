package com.banking.useraccounts.repository;


import com.banking.useraccounts.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
   UserRole findByName(String name);
}