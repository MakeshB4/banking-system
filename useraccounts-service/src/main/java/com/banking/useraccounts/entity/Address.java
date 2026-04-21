package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType = AddressType.PERMANENT;

    private Boolean isCommunicationAddress = true;

    public enum AddressType {
        PERMANENT,
        TEMPORARY,
        OFFICE
    }
}
