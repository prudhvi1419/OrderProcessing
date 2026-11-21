package org.example.orderprocessing.commission;

import org.example.orderprocessing.domain.CommissionBreakdown;
import org.example.orderprocessing.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommissionCalculatorTest {

    /** 100 per completed sale, same as the default in application.properties. */
    static CommissionProperties defaultProperties() {
        return new CommissionProperties(new BigDecimal("100"), Set.of(OrderStatus.COMPLETED));
    }

    private final CommissionCalculator calculator = new CommissionCalculator(defaultProperties());

    @Test
    void paysFixedAmountPerSale() {
        CommissionBreakdown result = calculator.calculate(new BigDecimal("12500"), 5);

        assertEquals(5, result.orderCount());
        assertEquals(new BigDecimal("100"), result.amountPerSale());
        assertEquals(new BigDecimal("500.00"), result.commissionAmount());
    }

    @Test
    void saleValueDoesNotChangeCommission() {
        BigDecimal small = calculator.calculate(new BigDecimal("500"), 19).commissionAmount();
        BigDecimal large = calculator.calculate(new BigDecimal("60369"), 19).commissionAmount();

        assertEquals(new BigDecimal("1900.00"), small);
        assertEquals(small, large);
    }

    @Test
    void noSalesPaysNothing() {
        assertEquals(new BigDecimal("0.00"), calculator.calculate(BigDecimal.ZERO, 0).commissionAmount());
    }

    @Test
    void amountPerSaleIsConfigurable() {
        CommissionCalculator custom = new CommissionCalculator(
                new CommissionProperties(new BigDecimal("250.50"), Set.of(OrderStatus.COMPLETED)));

        assertEquals(new BigDecimal("1002.00"), custom.calculate(new BigDecimal("1000"), 4).commissionAmount());
    }
}
