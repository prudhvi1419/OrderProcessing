package org.example.orderprocessing.service;

import org.example.orderprocessing.domain.Order;
import org.example.orderprocessing.domain.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.List;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order create(String id, OrderDetails details) {
        if (orderRepository.existsById(id)) {
            log.warn("Order already exists: orderId={}", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists");
        }
        Order order = orderRepository.save(newOrder(id, details));
        logSaved("Order created", order);
        return order;
    }

    @Transactional
    public Order update(String id, OrderDetails details) {
        Order order = findById(id);
        apply(order, details);
        logSaved("Order updated", order);
        return orderRepository.save(order);
    }

    /** Create or update; used for events from the upstream system. */
    @Transactional
    public Order upsert(String id, OrderDetails details) {
        Order order = orderRepository.findById(id)
                .map(existing -> {
                    apply(existing, details);
                    return existing;
                })
                .orElseGet(() -> newOrder(id, details));
        Order saved = orderRepository.save(order);
        logSaved("Order upserted from event", saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });
    }

    /** Orders for an advisor, optionally limited to one month. */
    @Transactional(readOnly = true)
    public List<Order> findByAdvisor(String advisorId, YearMonth period) {
        if (period == null) {
            return orderRepository.findByAdvisorIdOrderByOrderDateDesc(advisorId);
        }
        return orderRepository.findByAdvisorIdAndOrderDateBetweenOrderByOrderDateDesc(
                advisorId, period.atDay(1), period.atEndOfMonth());
    }

    private static Order newOrder(String id, OrderDetails d) {
        return new Order(id, d.advisorId(), d.customerId(), d.amount(), d.orderDate(), d.status());
    }

    private static void apply(Order order, OrderDetails d) {
        order.update(d.advisorId(), d.customerId(), d.amount(), d.orderDate(), d.status());
    }

    private static void logSaved(String message, Order order) {
        log.info("{}: orderId={}, advisorId={}, orderDate={}, status={}",
                message, order.getId(), order.getAdvisorId(), order.getOrderDate(), order.getStatus());
        log.debug("Order {} amount={}", order.getId(), order.getAmount());
    }
}
