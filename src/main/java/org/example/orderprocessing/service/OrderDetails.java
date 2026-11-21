package org.example.orderprocessing.service;

import org.example.orderprocessing.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Order fields shared by the REST API and the Kafka consumer. A null status defaults to PLACED. */
public record OrderDetails(
        String advisorId,
        String customerId,
        BigDecimal amount,
        LocalDate orderDate,
        OrderStatus status
) {
    public OrderDetails {
        status = status == null ? OrderStatus.PLACED : status;
    }
}
