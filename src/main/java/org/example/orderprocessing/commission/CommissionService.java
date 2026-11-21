package org.example.orderprocessing.commission;

import org.example.orderprocessing.domain.Advisor;
import org.example.orderprocessing.domain.AdvisorRepository;
import org.example.orderprocessing.domain.AdvisorSales;
import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.domain.CommissionBreakdown;
import org.example.orderprocessing.domain.CommissionResult;
import org.example.orderprocessing.domain.CommissionResultRepository;
import org.example.orderprocessing.domain.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CommissionService {
    private static final Logger log = LoggerFactory.getLogger(CommissionService.class);

    /** Stops the scheduled run and an on-demand run from overlapping (single application instance). */
    private final ReentrantLock runLock = new ReentrantLock();

    private final AdvisorRepository advisorRepository;
    private final OrderRepository orderRepository;
    private final CommissionResultRepository resultRepository;
    private final CommissionCalculator calculator;
    private final CommissionProperties properties;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    public CommissionService(
            AdvisorRepository advisorRepository,
            OrderRepository orderRepository,
            CommissionResultRepository resultRepository,
            CommissionCalculator calculator,
            CommissionProperties properties,
            TransactionTemplate transactionTemplate,
            Clock clock
    ) {
        this.advisorRepository = advisorRepository;
        this.orderRepository = orderRepository;
        this.resultRepository = resultRepository;
        this.calculator = calculator;
        this.properties = properties;
        this.transactionTemplate = transactionTemplate;
        this.clock = clock;
    }

    /** Calculates and stores commission for every active advisor for the given month. */
    public CommissionRunSummary run(YearMonth period) {
        if (period.isAfter(YearMonth.now(clock))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot calculate commission for a future period");
        }
        if (!runLock.tryLock()) {
            log.warn("Commission run for period {} rejected: another run is in progress", period);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A commission run is already in progress");
        }
        try {
            log.info("Commission run started for period {}", period);
            CommissionRunSummary summary = transactionTemplate.execute(status -> calculate(period));
            log.info("Commission run finished for period {}: advisors={}, skippedAdvisors={}",
                    period, summary.advisorsCalculated(), summary.skippedAdvisorIds().size());
            return summary;
        } finally {
            runLock.unlock();
        }
    }

    @Transactional(readOnly = true)
    public List<CommissionResult> findByPeriod(YearMonth period) {
        return resultRepository.findByPeriodOrderByAdvisorId(period.toString());
    }

    @Transactional(readOnly = true)
    public CommissionResult findByAdvisorAndPeriod(String advisorId, YearMonth period) {
        return resultRepository.findByAdvisorIdAndPeriod(advisorId, period.toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
    }

    private CommissionRunSummary calculate(YearMonth period) {
        Map<String, AdvisorSales> salesByAdvisor = new HashMap<>();
        for (AdvisorSales sales : orderRepository.summarizeSalesByAdvisor(
                period.atDay(1), period.atEndOfMonth(), properties.eligibleOrderStatuses())) {
            salesByAdvisor.put(sales.getAdvisorId(), sales);
        }

        List<Advisor> advisors = advisorRepository.findByStatus(AdvisorStatus.ACTIVE);
        Instant calculatedAt = Instant.now(clock);
        BigDecimal totalCommission = BigDecimal.ZERO;

        for (Advisor advisor : advisors) {
            AdvisorSales sales = salesByAdvisor.remove(advisor.getId());
            BigDecimal totalSales = sales == null ? BigDecimal.ZERO : sales.getTotalSales();
            long orderCount = sales == null ? 0 : sales.getOrderCount();

            CommissionBreakdown breakdown = calculator.calculate(totalSales, orderCount);

            CommissionResult result = resultRepository.findByAdvisorIdAndPeriod(advisor.getId(), period.toString())
                    .orElseGet(() -> new CommissionResult(advisor.getId(), period));
            result.apply(breakdown, calculatedAt);
            resultRepository.save(result);

            log.debug("Commission for advisorId={} period={}: sales={} x {} = {}",
                    advisor.getId(), period, orderCount, breakdown.amountPerSale(), breakdown.commissionAmount());
            totalCommission = totalCommission.add(breakdown.commissionAmount());
        }

        List<String> skipped = salesByAdvisor.keySet().stream().sorted().toList();
        if (!skipped.isEmpty()) {
            log.warn("Period {}: eligible orders found for {} unknown or inactive advisors, no commission calculated: {}",
                    period, skipped.size(), skipped);
        }
        return new CommissionRunSummary(period.toString(), advisors.size(), totalCommission, skipped, calculatedAt);
    }
}
