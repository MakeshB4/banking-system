package com.banking.useraccounts.service;

import com.banking.useraccounts.dto.UserDTO;
import com.banking.useraccounts.entity.UserInfo;
import com.banking.useraccounts.entity.UserRole;
import com.banking.useraccounts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfo registerUser(UserDTO userDTO) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userDTO.getUsername());
        userInfo.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userInfo.setRoles(userDTO.getRoles().stream()
                .map(roleDTO -> {
                    UserRole role = new UserRole();
                    role.setId(roleDTO.getId());
                    role.setName(roleDTO.getName());
                    return role;
                }).collect(Collectors.toSet()));
        return userRepository.save(userInfo);
    }

    public UserInfo updatePassword(Long userId, String newPassword) {
        UserInfo userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userInfo.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(userInfo);
    }
}