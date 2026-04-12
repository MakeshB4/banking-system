package com.cooperative.banking_system.controller;

import com.cooperative.banking_system.dto.ApiResponse;
import com.cooperative.banking_system.dto.ContactUpdateDto;
import com.cooperative.banking_system.dto.CustomerRequestDto;
import com.cooperative.banking_system.dto.CustomerResponseDto;
import com.cooperative.banking_system.dto.KycUpdateDto;
import com.cooperative.banking_system.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerRequestDto request) {
        CustomerResponseDto customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer onboarded successfully", customer));
    }

    @GetMapping("/{cifNumber}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> getCustomerByCif(
            @PathVariable String cifNumber) {
        CustomerResponseDto customer = customerService.getCustomerByCif(cifNumber);
        return ResponseEntity.ok(ApiResponse.success("Customer details fetched successfully", customer));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> getAllCustomers() {
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers fetched successfully", customers));
    }

    @PutMapping("/{cifNumber}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @PathVariable String cifNumber,
            @Valid @RequestBody CustomerRequestDto request) {
        CustomerResponseDto updated = customerService.updateCustomer(cifNumber, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updated));
    }

    @PatchMapping("/{cifNumber}/contact")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateContactInfo(
            @PathVariable String cifNumber,
            @Valid @RequestBody ContactUpdateDto request) {
        CustomerResponseDto updated = customerService.updateContactInfo(cifNumber, request);
        return ResponseEntity.ok(ApiResponse.success("Contact information updated successfully", updated));
    }

    @PatchMapping("/{cifNumber}/kyc")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateKycStatus(
            @PathVariable String cifNumber,
            @Valid @RequestBody KycUpdateDto request) {
        CustomerResponseDto updated = customerService.updateKycStatus(cifNumber, request);
        return ResponseEntity.ok(ApiResponse.success("KYC status updated successfully", updated));
    }

    @DeleteMapping("/{cifNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String cifNumber) {
        customerService.deleteCustomer(cifNumber);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
}
