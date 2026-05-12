package com.aiwalletplatform.payment.entity;

import com.aiwalletplatform.commons.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment Order Entity
 */
@Entity
@Table(name = "payment_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentOrder extends BaseEntity {

    @Column(unique = true)
    private String paymentOrderId;

    @Column
    private String senderWalletId;

    @Column
    private String receiverWalletId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency;

    @Column(length = 500)
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(length = 500)
    private String metadata;

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REVERSED
    }

    public enum PaymentMethod {
        WALLET_TRANSFER,
        BANK_TRANSFER,
        CARD,
        CRYPTO,
        OTHER
    }
}
