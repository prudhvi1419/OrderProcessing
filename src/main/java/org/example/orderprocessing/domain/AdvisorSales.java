package org.example.orderprocessing.domain;

import java.math.BigDecimal;

/** Per-advisor sales totals for a period, produced by {@link OrderRepository#summarizeSalesByAdvisor}. */
public interface AdvisorSales {
    String getAdvisorId();

    BigDecimal getTotalSales();

    Long getOrderCount();
}
