package org.example.orderprocessing.service;

import org.example.orderprocessing.domain.Advisor;
import org.example.orderprocessing.domain.AdvisorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdvisorService {
    private static final Logger log = LoggerFactory.getLogger(AdvisorService.class);

    private final AdvisorRepository advisorRepository;

    public AdvisorService(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }

    @Transactional
    public Advisor create(String id, AdvisorDetails details) {
        if (advisorRepository.existsById(id)) {
            log.warn("Advisor already exists: advisorId={}", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Advisor already exists");
        }
        Advisor advisor = advisorRepository.save(newAdvisor(id, details));
        log.info("Advisor created: advisorId={}, status={}", id, advisor.getStatus());
        return advisor;
    }

    @Transactional
    public Advisor update(String id, AdvisorDetails details) {
        Advisor advisor = findById(id);
        apply(advisor, details);
        log.info("Advisor updated: advisorId={}, status={}", id, advisor.getStatus());
        return advisorRepository.save(advisor);
    }

    /** Create or update; used for events from the upstream system. */
    @Transactional
    public Advisor upsert(String id, AdvisorDetails details) {
        Advisor advisor = advisorRepository.findById(id)
                .map(existing -> {
                    apply(existing, details);
                    log.info("Advisor updated from event: advisorId={}, status={}", id, existing.getStatus());
                    return existing;
                })
                .orElseGet(() -> {
                    log.info("Advisor created from event: advisorId={}, status={}", id, details.status());
                    return newAdvisor(id, details);
                });
        return advisorRepository.save(advisor);
    }

    @Transactional(readOnly = true)
    public Advisor findById(String id) {
        return advisorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Advisor not found: advisorId={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found");
                });
    }

    @Transactional(readOnly = true)
    public List<Advisor> findAll() {
        return advisorRepository.findAll();
    }

    private static Advisor newAdvisor(String id, AdvisorDetails d) {
        return new Advisor(id, d.name(), d.email(), d.status(), d.monthlyTarget());
    }

    private static void apply(Advisor advisor, AdvisorDetails d) {
        advisor.update(d.name(), d.email(), d.status(), d.monthlyTarget());
    }
}
