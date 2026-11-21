package org.example.orderprocessing.commission;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @param skippedAdvisorIds advisors that had eligible orders but are unknown or inactive, so got no commission
 */
public record CommissionRunSummary(
        String period,
        int advisorsCalculated,
        BigDecimal totalCommission,
        List<String> skippedAdvisorIds,
        Instant calculatedAt
) {
}
