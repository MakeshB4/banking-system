package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
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
