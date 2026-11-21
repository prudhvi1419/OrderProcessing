package org.example.orderprocessing.commission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.YearMonth;

/** Runs the commission calculation for the previous month on the configured cron schedule. */
@Component
public class CommissionScheduler {
    private static final Logger log = LoggerFactory.getLogger(CommissionScheduler.class);

    private final CommissionService commissionService;
    private final Clock clock;

    public CommissionScheduler(CommissionService commissionService, Clock clock) {
        this.commissionService = commissionService;
        this.clock = clock;
    }

    @Scheduled(cron = "${app.commission.schedule-cron}", zone = "${app.time-zone}")
    public void runForPreviousMonth() {
        YearMonth period = YearMonth.now(clock).minusMonths(1);
        log.info("Scheduled commission run triggered for period {}", period);
        try {
            commissionService.run(period);
        } catch (RuntimeException exception) {
            log.error("Scheduled commission run failed for period {}", period, exception);
        }
    }
}
