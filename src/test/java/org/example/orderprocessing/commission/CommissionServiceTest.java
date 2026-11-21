package org.example.orderprocessing.commission;

import org.example.orderprocessing.domain.Advisor;
import org.example.orderprocessing.domain.AdvisorRepository;
import org.example.orderprocessing.domain.AdvisorSales;
import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.domain.CommissionResult;
import org.example.orderprocessing.domain.CommissionResultRepository;
import org.example.orderprocessing.domain.OrderRepository;
import org.example.orderprocessing.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommissionServiceTest {
    private static final YearMonth PERIOD = YearMonth.of(2026, 10);

    @Mock
    private AdvisorRepository advisorRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CommissionResultRepository resultRepository;

    private CommissionService service;

    @BeforeEach
    void setUp() {
        CommissionProperties properties = CommissionCalculatorTest.defaultProperties();
        service = new CommissionService(
                advisorRepository,
                orderRepository,
                resultRepository,
                new CommissionCalculator(properties),
                properties,
                new TransactionTemplate(mock(PlatformTransactionManager.class)),
                Clock.fixed(Instant.parse("2026-11-21T10:00:00Z"), ZoneOffset.UTC)
        );
    }

    @Test
    void runCalculatesActiveAdvisorsAndReportsUnknownOnes() {
        Advisor withSales = new Advisor("ADV-1", "Jane", null, AdvisorStatus.ACTIVE, new BigDecimal("50000"));
        Advisor withoutSales = new Advisor("ADV-2", "John", null, AdvisorStatus.ACTIVE, null);
        CommissionResult existing = new CommissionResult("ADV-1", PERIOD);

        when(orderRepository.summarizeSalesByAdvisor(
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), Set.of(OrderStatus.COMPLETED)))
                .thenReturn(List.of(
                        new Sales("ADV-1", new BigDecimal("60000"), 55L),
                        new Sales("ADV-9", new BigDecimal("1000"), 1L)));
        when(advisorRepository.findByStatus(AdvisorStatus.ACTIVE)).thenReturn(List.of(withSales, withoutSales));
        when(resultRepository.findByAdvisorIdAndPeriod("ADV-1", "2026-10")).thenReturn(Optional.of(existing));
        when(resultRepository.findByAdvisorIdAndPeriod("ADV-2", "2026-10")).thenReturn(Optional.empty());

        CommissionRunSummary summary = service.run(PERIOD);

        assertEquals("2026-10", summary.period());
        assertEquals(2, summary.advisorsCalculated());
        assertEquals(new BigDecimal("5500.00"), summary.totalCommission());
        assertEquals(List.of("ADV-9"), summary.skippedAdvisorIds());

        ArgumentCaptor<CommissionResult> saved = ArgumentCaptor.forClass(CommissionResult.class);
        verify(resultRepository, times(2)).save(saved.capture());
        Map<String, CommissionResult> byAdvisor = saved.getAllValues().stream()
                .collect(Collectors.toMap(CommissionResult::getAdvisorId, Function.identity()));

        assertSame(existing, byAdvisor.get("ADV-1"), "existing result for the period is overwritten, not duplicated");
        assertEquals(new BigDecimal("5500.00"), byAdvisor.get("ADV-1").getCommissionAmount());
        assertEquals(55, byAdvisor.get("ADV-1").getOrderCount());
        assertEquals(new BigDecimal("0.00"), byAdvisor.get("ADV-2").getCommissionAmount());
        assertEquals(0, byAdvisor.get("ADV-2").getOrderCount());
        assertEquals("October 2026", byAdvisor.get("ADV-2").getMonth());
        assertEquals("2026-10", byAdvisor.get("ADV-2").getPeriod());
    }

    @Test
    void runRejectsFuturePeriod() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> service.run(YearMonth.of(2026, 12)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(orderRepository, advisorRepository, resultRepository);
    }

    @Test
    void runAllowsCurrentPeriod() {
        when(orderRepository.summarizeSalesByAdvisor(any(), any(), any())).thenReturn(List.of());
        when(advisorRepository.findByStatus(AdvisorStatus.ACTIVE)).thenReturn(List.of());

        CommissionRunSummary summary = service.run(YearMonth.of(2026, 11));

        assertEquals(0, summary.advisorsCalculated());
        assertEquals(BigDecimal.ZERO, summary.totalCommission());
    }

    private record Sales(String advisorId, BigDecimal totalSales, Long orderCount) implements AdvisorSales {
        @Override
        public String getAdvisorId() {
            return advisorId;
        }

        @Override
        public BigDecimal getTotalSales() {
            return totalSales;
        }

        @Override
        public Long getOrderCount() {
            return orderCount;
        }
    }
}
