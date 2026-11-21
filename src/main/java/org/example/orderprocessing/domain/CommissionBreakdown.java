package org.example.orderprocessing.domain;

import java.math.BigDecimal;

/**
 * Commission for one advisor: commissionAmount = orderCount x amountPerSale.
 *
 * @param totalSales value of the counted sales, for information only
 */
public record CommissionBreakdown(
        BigDecimal totalSales,
        long orderCount,
        BigDecimal amountPerSale,
        BigDecimal commissionAmount
) {
}
