package org.example.orderprocessing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "advisors")
public class Advisor {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvisorStatus status;

    /** Monthly sales target used for the target-achievement KPI. Null means no target. */
    @Column(precision = 19, scale = 2)
    private BigDecimal monthlyTarget;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Advisor() {
    }

    public Advisor(String id, String name, String email, AdvisorStatus status, BigDecimal monthlyTarget) {
        this.id = id;
        this.createdAt = Instant.now();
        update(name, email, status, monthlyTarget);
    }

    public void update(String name, String email, AdvisorStatus status, BigDecimal monthlyTarget) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.monthlyTarget = monthlyTarget;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public AdvisorStatus getStatus() {
        return status;
    }

    public BigDecimal getMonthlyTarget() {
        return monthlyTarget;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
