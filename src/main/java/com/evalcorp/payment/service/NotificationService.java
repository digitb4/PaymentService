package com.evalcorp.payment.service;

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

    @Autowired
    private PaymentService paymentService;

    public void sendPaymentConfirmation(String customerId, BigDecimal amount) {
        // Simulate sending email/SMS notification
        System.out.println("Payment confirmation sent to customer " + customerId + " for amount " + amount);
    }

    public void sendRefundNotification(String customerId, BigDecimal amount) {
        // Simulate sending refund notification
        System.out.println("Refund notification sent to customer " + customerId + " for amount " + amount);
    }

    /**
     * Resend notification for an existing payment — creates circular dependency
     * by calling back into PaymentService.
     */
    public void resendLastPaymentNotification(Long orderId) {
        // This creates the circular dependency: NotificationService → PaymentService
        // PaymentService already depends on NotificationService
        System.out.println("Resending notification for order: " + orderId);
    }
}
