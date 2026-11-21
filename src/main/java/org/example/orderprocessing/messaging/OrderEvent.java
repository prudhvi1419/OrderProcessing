package org.example.orderprocessing.messaging;

import org.example.orderprocessing.domain.OrderStatus;
import org.example.orderprocessing.service.OrderDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Order create/update message from the upstream system (topic {@code app.kafka.order-topic}).
 * Carries the full order state; the consumer creates or updates by orderId. Key should be orderId.
 *
 * <pre>{"orderId":"ORD-1","advisorId":"ADV-1","customerId":"CUST-9","amount":1250.00,"orderDate":"2026-11-03","status":"COMPLETED"}</pre>
 */
public record OrderEvent(
        String orderId,
        String advisorId,
        String customerId,
        BigDecimal amount,
        LocalDate orderDate,
        OrderStatus status
) {
    public OrderDetails toDetails() {
        return new OrderDetails(advisorId, customerId, amount, orderDate, status);
    }
}
