package com.cooperative.banking_system.entity;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accounts extends AccountBaseEntity {
    @Column(name = "accountName")
    private String name;
    @Column(name = "accountType")
    private String type;
    @Column(name = "accountBalance")
    private Double balance;
}
