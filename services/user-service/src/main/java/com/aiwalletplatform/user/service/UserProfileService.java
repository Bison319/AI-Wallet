package com.aiwalletplatform.user.service;

import com.aiwalletplatform.commons.dto.UserDTO;
import com.aiwalletplatform.commons.exception.ResourceNotFoundException;
import com.aiwalletplatform.user.entity.UserProfile;
import com.aiwalletplatform.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Profile Service - Business Logic for User Management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Get user profile by userId
     */
    @Cacheable(value = "userProfiles", key = "#userId")
    public UserProfile getUserProfile(String userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found: " + userId));
    }

    /**
     * Create or update user profile
     */
    @CacheEvict(value = "userProfiles", key = "#userProfile.userId")
    public UserProfile saveUserProfile(UserProfile userProfile) {
        log.info("Saving user profile for userId: {}", userProfile.getUserId());
        return userProfileRepository.save(userProfile);
    }

    /**
     * Update user profile
     */
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfile updateUserProfile(String userId, UserProfile updatedProfile) {
        UserProfile existing = getUserProfile(userId);
        
        if (updatedProfile.getFirstName() != null) {
            existing.setFirstName(updatedProfile.getFirstName());
        }
        if (updatedProfile.getLastName() != null) {
            existing.setLastName(updatedProfile.getLastName());
        }
        if (updatedProfile.getEmail() != null) {
            existing.setEmail(updatedProfile.getEmail());
        }
        if (updatedProfile.getPhone() != null) {
            existing.setPhone(updatedProfile.getPhone());
        }
        if (updatedProfile.getAddress() != null) {
            existing.setAddress(updatedProfile.getAddress());
        }
        if (updatedProfile.getCity() != null) {
            existing.setCity(updatedProfile.getCity());
        }
        if (updatedProfile.getState() != null) {
            existing.setState(updatedProfile.getState());
        }
        if (updatedProfile.getZipCode() != null) {
            existing.setZipCode(updatedProfile.getZipCode());
        }
        if (updatedProfile.getCountry() != null) {
            existing.setCountry(updatedProfile.getCountry());
        }

        log.info("Updated user profile for userId: {}", userId);
        return userProfileRepository.save(existing);
    }

    /**
     * Update KYC status
     */
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfile updateKYCStatus(String userId, UserProfile.KYCStatus status) {
        UserProfile profile = getUserProfile(userId);
        profile.setKycStatus(status);
        
        log.info("Updated KYC status for userId: {} to {}", userId, status);
        return userProfileRepository.save(profile);
    }

    /**
     * Update transaction limits
     */
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfile updateTransactionLimits(String userId, Long dailyLimit, Long monthlyLimit) {
        UserProfile profile = getUserProfile(userId);
        profile.setDailyLimit(dailyLimit);
        profile.setMonthlyLimit(monthlyLimit);
        
        log.info("Updated transaction limits for userId: {}, daily: {}, monthly: {}", 
                userId, dailyLimit, monthlyLimit);
        return userProfileRepository.save(profile);
    }
}
