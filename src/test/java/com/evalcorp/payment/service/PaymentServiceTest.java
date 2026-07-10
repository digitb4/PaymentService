package com.evalcorp.payment.service;

import com.evalcorp.payment.model.Order;
import com.evalcorp.payment.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId("CUST-001");
        order.setProductCode("PROD-100");
        order.setAmount(new BigDecimal("99.99"));
        order.setCurrency("USD");

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = paymentService.createOrder("CUST-001", "PROD-100", new BigDecimal("99.99"), "USD");

        assertNotNull(result);
        assertEquals("CUST-001", result.getCustomerId());
        assertEquals(new BigDecimal("99.99"), result.getAmount());
    }
}
