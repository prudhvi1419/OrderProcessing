package org.example.orderprocessing.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommissionResultRepository extends JpaRepository<CommissionResult, Long> {
    Optional<CommissionResult> findByAdvisorIdAndPeriod(String advisorId, String period);

    List<CommissionResult> findByPeriodOrderByAdvisorId(String period);
}
