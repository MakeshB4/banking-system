package com.banking.useraccounts.service;

import com.banking.useraccounts.entity.Cif;
import com.banking.useraccounts.entity.Customer;
import com.banking.useraccounts.exceptions.DetailsNotFoundException;
import com.banking.useraccounts.repository.CifRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CifServiceImplTest {

    @Mock
    private CifRepository cifRepository;

    @InjectMocks
    private CifServiceImpl cifService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
    }

    @Test
    void testCreateCif_Success() {
        Cif savedCif = new Cif();
        savedCif.setId(1L);
        savedCif.setCustomerNumber(20260421123456L);
        savedCif.setCustomer(customer);
        savedCif.setCustomerType(Cif.CustomerType.INDIVIDUAL);
        savedCif.setCifStatus(Cif.CifStatus.PENDING);
        savedCif.setRiskCategory("LOW");

        when(cifRepository.existsByCustomerNumber(anyLong())).thenReturn(false);
        when(cifRepository.save(any(Cif.class))).thenReturn(savedCif);

        Cif result = cifService.createCif(customer);

        assertNotNull(result);
        assertEquals("CIF20260421123456", result.getCustomerNumber());
        assertEquals(Cif.CustomerType.INDIVIDUAL, result.getCustomerType());
        assertEquals(Cif.CifStatus.PENDING, result.getCifStatus());
        assertEquals("LOW", result.getRiskCategory());

        verify(cifRepository, times(1)).save(any(Cif.class));
    }

    @Test
    void testGetCifByCifNumber_Success() {
        Cif cif = new Cif();
        cif.setCustomerNumber(20260421123456L);

        when(cifRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.of(cif));

        Cif result = cifService.getCifByCustomerNumber(20260421123456L);

        assertNotNull(result);
        assertEquals(20260421123456L, result.getCustomerNumber());
    }

    @Test
    void testGetCifByCifNumber_NotFound() {
        when(cifRepository.findByCustomerNumber(anyLong())).thenReturn(Optional.empty());

        DetailsNotFoundException exception = assertThrows(DetailsNotFoundException.class, () -> {
            cifService.getCifByCustomerNumber(99999999999999L);
        });

        assertEquals("CIF not found with number: CIF99999999999999", exception.getMessage());
    }

    @Test
    void testActivateCif_Success() {
        Cif cif = new Cif();
        cif.setId(1L);
        cif.setCustomerNumber(20260421123456L);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        when(cifRepository.findById(anyLong())).thenReturn(Optional.of(cif));
        when(cifRepository.save(any(Cif.class))).thenReturn(cif);

        cifService.activateCif(1L, "admin@bank.com");

        assertEquals(Cif.CifStatus.ACTIVE, cif.getCifStatus());
        assertEquals("admin@bank.com", cif.getApprovedBy());
        assertNotNull(cif.getActivationDate());
        verify(cifRepository, times(1)).save(cif);
    }

    @Test
    void testRejectCif_Success() {
        Cif cif = new Cif();
        cif.setId(1L);
        cif.setCustomerNumber(20260421123456L);
        cif.setCifStatus(Cif.CifStatus.PENDING);

        when(cifRepository.findById(anyLong())).thenReturn(Optional.of(cif));
        when(cifRepository.save(any(Cif.class))).thenReturn(cif);

        cifService.rejectCif(1L, "Invalid documents");

        assertEquals(Cif.CifStatus.REJECTED, cif.getCifStatus());
        assertEquals("Invalid documents", cif.getRemarks());
        verify(cifRepository, times(1)).save(cif);
    }
}