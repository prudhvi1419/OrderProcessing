package org.example.orderprocessing.api;

import jakarta.validation.Valid;
import org.example.orderprocessing.service.AdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/advisors")
public class AdvisorController {
    private static final Logger log = LoggerFactory.getLogger(AdvisorController.class);

    private final AdvisorService advisorService;

    public AdvisorController(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @PostMapping
    public ResponseEntity<AdvisorResponse> create(@Valid @RequestBody CreateAdvisorRequest request) {
        log.info("POST /api/advisors received: advisorId={}", request.advisorId());
        AdvisorResponse response = AdvisorResponse.from(advisorService.create(request.advisorId(), request.toDetails()));
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.advisorId())
                        .toUri()
        ).body(response);
    }

    @PutMapping("/{id}")
    public AdvisorResponse update(@PathVariable String id, @Valid @RequestBody UpdateAdvisorRequest request) {
        log.info("PUT /api/advisors/{} received", id);
        return AdvisorResponse.from(advisorService.update(id, request.toDetails()));
    }

    @GetMapping("/{id}")
    public AdvisorResponse findById(@PathVariable String id) {
        log.debug("GET /api/advisors/{} received", id);
        return AdvisorResponse.from(advisorService.findById(id));
    }

    @GetMapping
    public List<AdvisorResponse> findAll() {
        log.debug("GET /api/advisors received");
        return advisorService.findAll().stream().map(AdvisorResponse::from).toList();
    }
}
