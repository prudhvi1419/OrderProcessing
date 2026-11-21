package org.example.orderprocessing.service;

import org.example.orderprocessing.domain.Order;
import org.example.orderprocessing.domain.OrderRepository;
import org.example.orderprocessing.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private static final OrderDetails DETAILS = new OrderDetails(
            "ADV-1", "CUST-1", new BigDecimal("1250.00"), LocalDate.of(2026, 11, 3), null);

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createSavesNewOrderWithDefaultStatus() {
        when(orderRepository.existsById("ORD-1")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.create("ORD-1", DETAILS);

        assertEquals("ORD-1", order.getId());
        assertEquals("ADV-1", order.getAdvisorId());
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void createRejectsDuplicateOrder() {
        when(orderRepository.existsById("ORD-1")).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> orderService.create("ORD-1", DETAILS));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateRejectsMissingOrder() {
        when(orderRepository.findById("ORD-1")).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> orderService.update("ORD-1", DETAILS));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void upsertUpdatesExistingOrder() {
        Order existing = new Order("ORD-1", "ADV-1", "CUST-1", new BigDecimal("1250.00"),
                LocalDate.of(2026, 11, 3), OrderStatus.PLACED);
        when(orderRepository.findById("ORD-1")).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        Order order = orderService.upsert("ORD-1", new OrderDetails(
                "ADV-1", "CUST-1", new BigDecimal("1250.00"), LocalDate.of(2026, 11, 3), OrderStatus.CANCELLED));

        assertSame(existing, order);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void upsertCreatesMissingOrder() {
        when(orderRepository.findById("ORD-2")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.upsert("ORD-2", DETAILS);

        assertEquals("ORD-2", order.getId());
        assertEquals(new BigDecimal("1250.00"), order.getAmount());
    }
}
