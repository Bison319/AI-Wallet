package com.aiwalletplatform.notification.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Event Listener for Notifications
 */
@Service
@Slf4j
public class NotificationEventListener {

    @KafkaListener(topics = "wallet-events", groupId = "notification-service")
    public void handleWalletEvent(String message) {
        log.info("Received wallet event: {}", message);
        // Send notification
    }

    @KafkaListener(topics = "transaction-events", groupId = "notification-service")
    public void handleTransactionEvent(String message) {
        log.info("Received transaction event: {}", message);
        // Send notification
    }

    @KafkaListener(topics = "fraud-events", groupId = "notification-service")
    public void handleFraudEvent(String message) {
        log.info("Received fraud event: {}", message);
        // Send alert notification
    }
}
