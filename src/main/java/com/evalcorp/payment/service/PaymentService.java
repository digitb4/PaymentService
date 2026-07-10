package com.evalcorp.payment.service;

import com.evalcorp.payment.model.Order;
import com.evalcorp.payment.model.Payment;
import com.evalcorp.payment.repository.OrderRepository;
import com.evalcorp.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationService notificationService;

    // [REL] NPE — Optional.get() without isPresent() check
    public Payment processRefund(Long orderId) {
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);

        // BUG: Calling .get() without checking isPresent() — will throw NoSuchElementException
        Payment existingPayment = payment.get();

        existingPayment.setStatus(Payment.PaymentStatus.REFUNDED);
        existingPayment.setProcessedAt(LocalDateTime.now());

        Order order = orderRepository.findById(orderId).get();
        order.setStatus(Order.OrderStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        notificationService.sendRefundNotification(order.getCustomerId(), existingPayment.getAmount());

        return paymentRepository.save(existingPayment);
    }

    public Payment processPayment(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(order.getAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());

        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        notificationService.sendPaymentConfirmation(order.getCustomerId(), payment.getAmount());

        return paymentRepository.save(payment);
    }

    public Order createOrder(String customerId, String productCode, BigDecimal amount, String currency) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setProductCode(productCode);
        order.setAmount(amount);
        order.setCurrency(currency);
        return orderRepository.save(order);
    }
}
