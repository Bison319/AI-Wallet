package com.aiwalletplatform.payment.repository;

import com.aiwalletplatform.payment.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Payment Order Repository
 */
@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    Optional<PaymentOrder> findByPaymentOrderId(String paymentOrderId);
    java.util.List<PaymentOrder> findBySenderWalletId(String senderWalletId);
    java.util.List<PaymentOrder> findByReceiverWalletId(String receiverWalletId);
}
