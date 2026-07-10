package com.evalcorp.payment.repository;

import com.evalcorp.payment.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByStatus(Order.OrderStatus status);
}
