package org.example.orderprocessing.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.orderprocessing.domain.OrderStatus;
import org.example.orderprocessing.service.OrderDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Full replacement of an order's fields. */
public record UpdateOrderRequest(
        @NotBlank String advisorId,
        @NotBlank String customerId,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate orderDate,
        @NotNull OrderStatus status
) {
    public OrderDetails toDetails() {
        return new OrderDetails(advisorId, customerId, amount, orderDate, status);
    }
}
