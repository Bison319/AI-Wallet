package com.aiwalletplatform.identity.service;

import com.aiwalletplatform.identity.dto.UserResponse;
import com.aiwalletplatform.identity.entity.UserAuth;
import com.aiwalletplatform.identity.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * User Authentication Service
 * Manages user data and authentication records
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    /**
     * Find user by email
     */
    public Optional<UserResponse> findByEmail(String email) {
        return userAuthRepository.findByEmail(email)
            .map(this::toUserResponse);
    }

    /**
     * Find user by ID
     */
    public Optional<UserResponse> findById(String id) {
        return userAuthRepository.findById(id)
            .map(this::toUserResponse);
    }

    /**
     * Create new user
     */
    @Transactional
    public UserResponse createUser(String email, String passwordHash, String firstName, 
                                   String lastName, String phone) {
        UserAuth user = UserAuth.builder()
            .id(UUID.randomUUID().toString())
            .email(email)
            .passwordHash(passwordHash)
            .firstName(firstName)
            .lastName(lastName)
            .phone(phone)
            .role("USER")
            .accountStatus("ACTIVE")
            .enabled(true)
            .build();

        user = userAuthRepository.save(user);
        log.info("New user created with ID: {}", user.getId());

        return toUserResponse(user);
    }

    /**
     * Convert entity to DTO
     */
    private UserResponse toUserResponse(UserAuth user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getRole(),
            user.getAccountStatus()
        );
    }
}
