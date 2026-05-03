package com.banking.useraccounts.config;

import com.banking.useraccounts.entity.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.banking.useraccounts.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (!roleRepository.existsById(1L)) {
                roleRepository.save(new UserRole(1L, "USER"));
            }
            if (!roleRepository.existsById(2L)) {
                roleRepository.save(new UserRole(2L, "ADMIN"));
            }
        };
    }
}