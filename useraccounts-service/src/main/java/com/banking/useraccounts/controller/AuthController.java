package com.banking.useraccounts.controller;


import com.banking.useraccounts.dto.UserDTO;
import com.banking.useraccounts.dto.request.AuthRequestDTO;
import com.banking.useraccounts.dto.response.JwtResponseDTO;
import com.banking.useraccounts.entity.UserInfo;
import com.banking.useraccounts.service.JwtService;
import com.banking.useraccounts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

     @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(JwtResponseDTO.builder()
                    .accessToken(jwtService.generateToken(
                        request.getUsername(),
                        authentication.getAuthorities()))
                    .build());
        }
        throw new UsernameNotFoundException("Invalid credentials!");
    }

    @PostMapping("/register")
    public ResponseEntity<UserInfo> register(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.registerUser(userDTO));
    }

    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<UserInfo> resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(userId, newPassword));
    }
}