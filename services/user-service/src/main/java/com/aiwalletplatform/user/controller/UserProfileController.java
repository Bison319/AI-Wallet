package com.aiwalletplatform.user.controller;

import com.aiwalletplatform.commons.dto.ApiResponse;
import com.aiwalletplatform.user.entity.UserProfile;
import com.aiwalletplatform.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Profile REST Controller
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Get user profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> getUserProfile(@PathVariable String userId) {
        UserProfile profile = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Create or update user profile
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> createUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile saved = userProfileService.saveUserProfile(userProfile);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    /**
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> updateUserProfile(
            @PathVariable String userId,
            @RequestBody UserProfile updatedProfile) {
        UserProfile updated = userProfileService.updateUserProfile(userId, updatedProfile);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Update KYC status
     */
    @PutMapping("/{userId}/kyc/{status}")
    public ResponseEntity<ApiResponse<UserProfile>> updateKYCStatus(
            @PathVariable String userId,
            @PathVariable UserProfile.KYCStatus status) {
        UserProfile updated = userProfileService.updateKYCStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Update transaction limits
     */
    @PutMapping("/{userId}/limits")
    public ResponseEntity<ApiResponse<UserProfile>> updateLimits(
            @PathVariable String userId,
            @RequestParam Long dailyLimit,
            @RequestParam Long monthlyLimit) {
        UserProfile updated = userProfileService.updateTransactionLimits(userId, dailyLimit, monthlyLimit);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}
