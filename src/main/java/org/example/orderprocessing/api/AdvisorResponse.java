package org.example.orderprocessing.api;

import org.example.orderprocessing.domain.Advisor;
import org.example.orderprocessing.domain.AdvisorStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AdvisorResponse(
        String advisorId,
        String name,
        String email,
        AdvisorStatus status,
        BigDecimal monthlyTarget,
        Instant createdAt,
        Instant updatedAt
) {
    public static AdvisorResponse from(Advisor advisor) {
        return new AdvisorResponse(
                advisor.getId(),
                advisor.getName(),
                advisor.getEmail(),
                advisor.getStatus(),
                advisor.getMonthlyTarget(),
                advisor.getCreatedAt(),
                advisor.getUpdatedAt()
        );
    }
}
