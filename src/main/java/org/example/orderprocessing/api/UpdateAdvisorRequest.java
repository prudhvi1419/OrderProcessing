package org.example.orderprocessing.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.service.AdvisorDetails;

import java.math.BigDecimal;

/** Full replacement of an advisor's fields. */
public record UpdateAdvisorRequest(
        @NotBlank String name,
        @Email String email,
        @NotNull AdvisorStatus status,
        @PositiveOrZero BigDecimal monthlyTarget
) {
    public AdvisorDetails toDetails() {
        return new AdvisorDetails(name, email, status, monthlyTarget);
    }
}
