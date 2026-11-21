package org.example.orderprocessing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_advisor_date", columnList = "advisor_id, order_date"),
        @Index(name = "idx_orders_date", columnList = "order_date")
})
public class Order {
    @Id
    private String id;

    @Column(name = "advisor_id", nullable = false)
    private String advisorId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Order() {
    }

    public Order(String id, String advisorId, String customerId, BigDecimal amount,
                 LocalDate orderDate, OrderStatus status) {
        this.id = id;
        this.createdAt = Instant.now();
        update(advisorId, customerId, amount, orderDate, status);
    }

    public void update(String advisorId, String customerId, BigDecimal amount,
                       LocalDate orderDate, OrderStatus status) {
        this.advisorId = advisorId;
        this.customerId = customerId;
        this.amount = amount;
        this.orderDate = orderDate;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getAdvisorId() {
        return advisorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
