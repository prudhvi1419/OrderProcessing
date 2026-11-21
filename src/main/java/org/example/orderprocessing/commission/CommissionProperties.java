package org.example.orderprocessing.commission;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.orderprocessing.domain.OrderStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Commission rules, bound from {@code app.commission.*}.
 *
 * <p>commission = number of eligible sales in the month x amountPerSale
 *
 * @param amountPerSale         fixed amount paid for each eligible sale (e.g. 100)
 * @param eligibleOrderStatuses order statuses that count as a sale (e.g. COMPLETED)
 */
@Validated
@ConfigurationProperties(prefix = "app.commission")
public record CommissionProperties(
        @NotNull @PositiveOrZero BigDecimal amountPerSale,
        @NotEmpty Set<OrderStatus> eligibleOrderStatuses
) {
}
