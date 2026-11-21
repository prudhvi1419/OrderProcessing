package org.example.orderprocessing.messaging;

import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.service.AdvisorDetails;

import java.math.BigDecimal;

/**
 * Advisor create/update message from the upstream system (topic {@code app.kafka.advisor-topic}).
 * Carries the full advisor state; the consumer creates or updates by advisorId. Key should be advisorId.
 *
 * <pre>{"advisorId":"ADV-1","name":"Jane Doe","email":"jane@example.com","status":"ACTIVE","monthlyTarget":50000}</pre>
 */
public record AdvisorEvent(
        String advisorId,
        String name,
        String email,
        AdvisorStatus status,
        BigDecimal monthlyTarget
) {
    public AdvisorDetails toDetails() {
        return new AdvisorDetails(name, email, status, monthlyTarget);
    }
}
