package org.example.orderprocessing.api;

import org.example.orderprocessing.domain.Order;
import org.example.orderprocessing.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OrderResponse(
        String orderId,
        String advisorId,
        String customerId,
        BigDecimal amount,
        LocalDate orderDate,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getAdvisorId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getOrderDate(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
