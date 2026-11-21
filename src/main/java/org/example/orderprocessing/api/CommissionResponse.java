package org.example.orderprocessing.api;

import org.example.orderprocessing.domain.CommissionResult;

import java.math.BigDecimal;
import java.time.Instant;

public record CommissionResponse(
        String month,
        String period,
        String advisorId,
        long orderCount,
        BigDecimal amountPerSale,
        BigDecimal commissionAmount,
        BigDecimal totalSales,
        Instant calculatedAt
) {
    public static CommissionResponse from(CommissionResult result) {
        return new CommissionResponse(
                result.getMonth(),
                result.getPeriod(),
                result.getAdvisorId(),
                result.getOrderCount(),
                result.getAmountPerSale(),
                result.getCommissionAmount(),
                result.getTotalSales(),
                result.getCalculatedAt()
        );
    }
}
