package org.example.orderprocessing.commission;

import org.example.orderprocessing.domain.CommissionBreakdown;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** commission = number of sales x amount per sale. Pure calculation, no I/O. */
@Component
public class CommissionCalculator {
    private final CommissionProperties properties;

    public CommissionCalculator(CommissionProperties properties) {
        this.properties = properties;
    }

    public CommissionBreakdown calculate(BigDecimal totalSales, long orderCount) {
        BigDecimal amountPerSale = properties.amountPerSale();
        BigDecimal commission = amountPerSale.multiply(BigDecimal.valueOf(orderCount))
                .setScale(2, RoundingMode.HALF_UP);
        return new CommissionBreakdown(
                totalSales.setScale(2, RoundingMode.HALF_UP),
                orderCount,
                amountPerSale,
                commission
        );
    }
}
