package com.evalcorp.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Notification service for sending payment and refund notifications.
 * [ARCH] Circular dependency — NotificationService depends on PaymentService
 * and PaymentService depends on NotificationService.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private PaymentService paymentService;

    public void sendPaymentConfirmation(String customerId, BigDecimal amount) {
        // Simulate sending email/SMS notification
        logger.info("Payment confirmation sent to customer {} for amount {}", customerId, amount);
    }

    public void sendRefundNotification(String customerId, BigDecimal amount) {
        // Simulate sending refund notification
        logger.info("Refund notification sent to customer {} for amount {}", customerId, amount);
    }

    /**
     * Resend notification for an existing payment — creates circular dependency
     * by calling back into PaymentService.
     */
    public void resendLastPaymentNotification(Long orderId) {
        // This creates the circular dependency: NotificationService → PaymentService
        // PaymentService already depends on NotificationService
        logger.info("Resending notification for order: {}", orderId);
    }
}
