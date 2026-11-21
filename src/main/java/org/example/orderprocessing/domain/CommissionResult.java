package org.example.orderprocessing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Commission calculated for one advisor in one period (yyyy-MM). Re-running a period overwrites it.
 * The table is defined in schema.sql so the month columns come first.
 */
@Entity
@Table(name = "commission_results", uniqueConstraints =
        @UniqueConstraint(name = "uk_commission_advisor_period", columnNames = {"advisor_id", "commission_period"}))
public class CommissionResult {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advisor_id", nullable = false)
    private String advisorId;

    @Column(name = "commission_period", nullable = false, length = 7)
    private String period;

    /** Readable month, e.g. "October 2026". */
    @Column(name = "commission_month", nullable = false, length = 20)
    private String month;

    @Column(nullable = false)
    private long orderCount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountPerSale;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSales;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal commissionAmount;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected CommissionResult() {
    }

    public CommissionResult(String advisorId, YearMonth period) {
        this.advisorId = advisorId;
        this.period = period.toString();
        this.month = period.format(MONTH_FORMAT);
    }

    public void apply(CommissionBreakdown breakdown, Instant calculatedAt) {
        this.orderCount = breakdown.orderCount();
        this.amountPerSale = breakdown.amountPerSale();
        this.totalSales = breakdown.totalSales();
        this.commissionAmount = breakdown.commissionAmount();
        this.calculatedAt = calculatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getAdvisorId() {
        return advisorId;
    }

    public String getPeriod() {
        return period;
    }

    public String getMonth() {
        return month;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public BigDecimal getAmountPerSale() {
        return amountPerSale;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
