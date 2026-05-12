package com.aiwalletplatform.identity.repository;

import com.aiwalletplatform.identity.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserAuth Repository
 * Data access layer for authentication users
 */
@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, String> {

    /**
     * Find user by email
     */
    Optional<UserAuth> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find user by phone
     */
    Optional<UserAuth> findByPhone(String phone);
}
