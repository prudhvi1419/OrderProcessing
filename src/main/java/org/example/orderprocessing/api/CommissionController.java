package org.example.orderprocessing.api;

import org.example.orderprocessing.commission.CommissionRunSummary;
import org.example.orderprocessing.commission.CommissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {
    private static final Logger log = LoggerFactory.getLogger(CommissionController.class);

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    /** On-demand run: POST /api/commissions/runs?period=2026-11. Re-running a period overwrites its results. */
    @PostMapping("/runs")
    public CommissionRunSummary run(@RequestParam YearMonth period) {
        log.info("POST /api/commissions/runs received: period={}", period);
        return commissionService.run(period);
    }

    /** GET /api/commissions?period=2026-11 */
    @GetMapping
    public List<CommissionResponse> findByPeriod(@RequestParam YearMonth period) {
        log.debug("GET /api/commissions received: period={}", period);
        return commissionService.findByPeriod(period).stream().map(CommissionResponse::from).toList();
    }

    /** GET /api/commissions/ADV-1/2026-11 */
    @GetMapping("/{advisorId}/{period}")
    public CommissionResponse findByAdvisorAndPeriod(@PathVariable String advisorId, @PathVariable YearMonth period) {
        log.debug("GET /api/commissions/{}/{} received", advisorId, period);
        return CommissionResponse.from(commissionService.findByAdvisorAndPeriod(advisorId, period));
    }
}
