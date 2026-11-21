package org.example.orderprocessing.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.service.AdvisorDetails;

import java.math.BigDecimal;

public record CreateAdvisorRequest(
        @NotBlank String advisorId,
        @NotBlank String name,
        @Email String email,
        AdvisorStatus status,
        @PositiveOrZero BigDecimal monthlyTarget
) {
    public AdvisorDetails toDetails() {
        return new AdvisorDetails(name, email, status, monthlyTarget);
    }
}
