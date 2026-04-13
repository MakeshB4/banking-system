package com.cooperative.banking_system.service;

import com.cooperative.banking_system.dto.ContactUpdateDto;
import com.cooperative.banking_system.dto.CustomerRequestDto;
import com.cooperative.banking_system.dto.CustomerResponseDto;
import com.cooperative.banking_system.dto.KycUpdateDto;
import com.cooperative.banking_system.entity.Customer;
import com.cooperative.banking_system.exception.DuplicateResourceException;
import com.cooperative.banking_system.exception.ResourceNotFoundException;
import com.cooperative.banking_system.repository.CustomerRepository;
import com.cooperative.banking_system.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequestDto requestDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCifNumber("CIF123456789");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("+1234567890");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 15));
        customer.setGender("MALE");
        customer.setAddress("123 Main St");
        customer.setKycStatus(Customer.KycStatus.PENDING);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setDelFlag("N");

        requestDto = new CustomerRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@example.com");
        requestDto.setPhoneNumber("+1234567890");
        requestDto.setDateOfBirth(LocalDate.of(1990, 1, 15));
        requestDto.setGender("MALE");
        requestDto.setAddress("123 Main St");
    }

    // --- createCustomer ---

    @Test
    void createCustomer_success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByCifNumber(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDto result = customerService.createCustomer(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getCifNumber()).isEqualTo("CIF123456789");
        assertThat(result.getKycStatus()).isEqualTo(Customer.KycStatus.PENDING);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_duplicateEmail_throwsDuplicateResourceException() {
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john.doe@example.com");

        verify(customerRepository, never()).save(any());
    }

    // --- getCustomerByCif ---

    @Test
    void getCustomerByCif_success() {
        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));

        CustomerResponseDto result = customerService.getCustomerByCif("CIF123456789");

        assertThat(result.getCifNumber()).isEqualTo("CIF123456789");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getDelFlag()).isEqualTo("N");
    }

    @Test
    void getCustomerByCif_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByCifNumberAndDelFlag("INVALID", "N")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByCif("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("INVALID");
    }

    @Test
    void getCustomerByCif_softDeleted_throwsResourceNotFoundException() {
        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByCif("CIF123456789"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("CIF123456789");
    }

    // --- getAllCustomers ---

    @Test
    void getAllCustomers_returnsActiveCustomersOnly() {
        when(customerRepository.findByDelFlag("N")).thenReturn(List.of(customer));

        List<CustomerResponseDto> result = customerService.getAllCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.get(0).getDelFlag()).isEqualTo("N");
    }

    @Test
    void getAllCustomers_empty_returnsEmptyList() {
        when(customerRepository.findByDelFlag("N")).thenReturn(List.of());

        List<CustomerResponseDto> result = customerService.getAllCustomers();

        assertThat(result).isEmpty();
    }

    // --- updateCustomer ---

    @Test
    void updateCustomer_success() {
        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDto result = customerService.updateCustomer("CIF123456789", requestDto);

        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_emailTakenByAnother_throwsDuplicateResourceException() {
        requestDto.setEmail("other@example.com");
        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("other@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.updateCustomer("CIF123456789", requestDto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // --- updateContactInfo ---

    @Test
    void updateContactInfo_phoneOnly_updatesPhone() {
        ContactUpdateDto contactDto = new ContactUpdateDto();
        contactDto.setPhoneNumber("+9999999999");

        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto result = customerService.updateContactInfo("CIF123456789", contactDto);

        assertThat(result.getPhoneNumber()).isEqualTo("+9999999999");
    }

    @Test
    void updateContactInfo_duplicateEmail_throwsDuplicateResourceException() {
        ContactUpdateDto contactDto = new ContactUpdateDto();
        contactDto.setEmail("taken@example.com");

        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.updateContactInfo("CIF123456789", contactDto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // --- updateKycStatus ---

    @Test
    void updateKycStatus_toVerified_success() {
        KycUpdateDto kycDto = new KycUpdateDto();
        kycDto.setKycStatus(Customer.KycStatus.VERIFIED);

        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponseDto result = customerService.updateKycStatus("CIF123456789", kycDto);

        assertThat(result.getKycStatus()).isEqualTo(Customer.KycStatus.VERIFIED);
    }

    @Test
    void updateKycStatus_customerNotFound_throwsResourceNotFoundException() {
        KycUpdateDto kycDto = new KycUpdateDto();
        kycDto.setKycStatus(Customer.KycStatus.VERIFIED);

        when(customerRepository.findByCifNumberAndDelFlag("INVALID", "N")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateKycStatus("INVALID", kycDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- deleteCustomer ---

    @Test
    void deleteCustomer_success_setsDelFlagToY() {
        when(customerRepository.findByCifNumberAndDelFlag("CIF123456789", "N")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        customerService.deleteCustomer("CIF123456789");

        assertThat(customer.getDelFlag()).isEqualTo("Y");
        verify(customerRepository).save(customer);
        verify(customerRepository, never()).delete(any());
    }

    @Test
    void deleteCustomer_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findByCifNumberAndDelFlag("INVALID", "N")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomer("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("INVALID");
    }
}
