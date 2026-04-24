package com.banking.useraccounts.controller;

import com.banking.useraccounts.dto.request.UserModificationRequest;
import com.banking.useraccounts.dto.request.UserRegistrationRequest;
import com.banking.useraccounts.dto.response.PendingCustomerResponse;
import com.banking.useraccounts.dto.response.UserRegistrationResponse;
import com.banking.useraccounts.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received user registration request for email: {}", request.getEmail());

        UserRegistrationResponse response = userRegistrationService.registerUser(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/pending/{cifNumber}")
    public ResponseEntity<PendingCustomerResponse> getPendingCifNumber(@PathVariable String cifNumber) {
        log.info("Admin fetching pending customer with cifNumber: {}", cifNumber);

        PendingCustomerResponse response = userRegistrationService.getPendingCustomerById(cifNumber);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UserRegistrationResponse> updateUser(
            @Valid @RequestBody UserModificationRequest request) {
        log.info("Received user update request for CIF: {}", request.getCifNumber());

        UserRegistrationResponse response = userRegistrationService.updateUser(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}