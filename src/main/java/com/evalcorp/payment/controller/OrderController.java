package com.evalcorp.payment.controller;

import com.evalcorp.payment.model.Order;
import com.evalcorp.payment.model.Payment;
import com.evalcorp.payment.repository.OrderRepository;
import com.evalcorp.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // [ARCH] Controller directly injects Repository (bypasses Service layer)
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DataSource dataSource;

    // [SEC] SQL injection — customerId concatenated into raw JDBC query
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(@RequestParam String customerId) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            // BUG: SQL injection via string concatenation
            String query = "SELECT * FROM orders WHERE customer_id = '" + customerId + "'";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                results.add(Map.of(
                    "id", rs.getLong("id"),
                    "customerId", rs.getString("customer_id"),
                    "amount", rs.getBigDecimal("amount")
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(results);
    }

    // [REL] Missing input validation — negative amounts accepted
    // [REL] NPE — order.getProductCode().toUpperCase() with no null check
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> request) {
        String customerId = (String) request.get("customerId");
        String productCode = (String) request.get("productCode");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = (String) request.get("currency");

        // BUG: No validation — negative amounts are accepted
        // BUG: NPE if productCode is null
        String normalizedProductCode = productCode.toUpperCase();

        Order order = paymentService.createOrder(customerId, normalizedProductCode, amount, currency);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Payment> processPayment(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        String paymentMethod = request.get("paymentMethod");
        Payment payment = paymentService.processPayment(orderId, paymentMethod);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Payment> processRefund(@PathVariable Long orderId) {
        Payment payment = paymentService.processRefund(orderId);
        return ResponseEntity.ok(payment);
    }

    // Uses repository directly instead of going through service layer
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

    // New endpoint added in this PR — contains a SQL injection vulnerability
    @GetMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> filterOrders(
            @RequestParam String status,
            @RequestParam(required = false) String sortBy) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            // SQL injection via string concatenation
            String query = "SELECT * FROM orders WHERE status = '" + status + "' ORDER BY " + sortBy;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                results.add(Map.of(
                    "id", rs.getLong("id"),
                    "status", rs.getString("status"),
                    "amount", rs.getBigDecimal("amount")
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(results);
    }
