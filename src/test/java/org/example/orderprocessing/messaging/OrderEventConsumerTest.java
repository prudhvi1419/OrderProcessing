package org.example.orderprocessing.messaging;

import org.example.orderprocessing.domain.OrderStatus;
import org.example.orderprocessing.service.OrderDetails;
import org.example.orderprocessing.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {
    @Mock
    private OrderService orderService;

    @Test
    void consumeUpsertsOrder() {
        OrderEventConsumer consumer = new OrderEventConsumer(orderService);
        LocalDate date = LocalDate.of(2026, 11, 3);

        consumer.consume(new OrderEvent("ORD-1", "ADV-1", "CUST-1", new BigDecimal("1250.00"), date, OrderStatus.COMPLETED));

        verify(orderService).upsert("ORD-1",
                new OrderDetails("ADV-1", "CUST-1", new BigDecimal("1250.00"), date, OrderStatus.COMPLETED));
    }

    @Test
    void consumeRejectsEventWithoutAdvisor() {
        OrderEventConsumer consumer = new OrderEventConsumer(orderService);
        OrderEvent event = new OrderEvent("ORD-1", null, "CUST-1", new BigDecimal("1250.00"),
                LocalDate.of(2026, 11, 3), OrderStatus.COMPLETED);

        assertThrows(IllegalArgumentException.class, () -> consumer.consume(event));
        verifyNoInteractions(orderService);
    }

    @Test
    void consumeRejectsNonPositiveAmount() {
        OrderEventConsumer consumer = new OrderEventConsumer(orderService);
        OrderEvent event = new OrderEvent("ORD-1", "ADV-1", "CUST-1", BigDecimal.ZERO,
                LocalDate.of(2026, 11, 3), OrderStatus.COMPLETED);

        assertThrows(IllegalArgumentException.class, () -> consumer.consume(event));
        verifyNoInteractions(orderService);
    }
}
