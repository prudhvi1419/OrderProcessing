package org.example.orderprocessing.service;

import org.example.orderprocessing.domain.AdvisorStatus;

import java.math.BigDecimal;

/** Advisor fields shared by the REST API and the Kafka consumer. A null status defaults to ACTIVE. */
public record AdvisorDetails(String name, String email, AdvisorStatus status, BigDecimal monthlyTarget) {
    public AdvisorDetails {
        status = status == null ? AdvisorStatus.ACTIVE : status;
    }
}
