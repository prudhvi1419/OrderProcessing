package org.example.orderprocessing.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvisorRepository extends JpaRepository<Advisor, String> {
    List<Advisor> findByStatus(AdvisorStatus status);
}
