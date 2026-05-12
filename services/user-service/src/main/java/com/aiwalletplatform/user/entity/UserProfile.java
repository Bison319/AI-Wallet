package com.aiwalletplatform.user.entity;

import com.aiwalletplatform.commons.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User Profile Entity
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String zipCode;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus;

    @Column(length = 500)
    private String kycDocument;

    @Column
    private Long dailyLimit;

    @Column
    private Long monthlyLimit;

    @Column(length = 500)
    private String preferences;

    @Column
    private Boolean emailNotificationsEnabled = true;

    @Column
    private Boolean smsNotificationsEnabled = true;

    @Column
    private Boolean pushNotificationsEnabled = true;

    public enum KYCStatus {
        PENDING,
        VERIFIED,
        REJECTED,
        EXPIRED
    }
}
