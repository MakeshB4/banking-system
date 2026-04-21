package com.banking.useraccounts.repository;

import com.banking.useraccounts.entity.Cif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CifRepository extends JpaRepository<Cif, Long> {

    Optional<Cif> findByCifNumber(String cifNumber);

    Optional<Cif> findByCustomerId(Long customerId);

    List<Cif> findByCifStatus(Cif.CifStatus cifStatus);

    boolean existsByCifNumber(String cifNumber);
}
