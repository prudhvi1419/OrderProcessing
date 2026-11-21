package org.example.orderprocessing.messaging;

import org.example.orderprocessing.domain.AdvisorStatus;
import org.example.orderprocessing.service.AdvisorDetails;
import org.example.orderprocessing.service.AdvisorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AdvisorEventConsumerTest {
    @Mock
    private AdvisorService advisorService;

    @Test
    void consumeUpsertsAdvisorDefaultingToActive() {
        AdvisorEventConsumer consumer = new AdvisorEventConsumer(advisorService);

        consumer.consume(new AdvisorEvent("ADV-1", "Jane Doe", "jane@example.com", null, new BigDecimal("50000")));

        verify(advisorService).upsert("ADV-1",
                new AdvisorDetails("Jane Doe", "jane@example.com", AdvisorStatus.ACTIVE, new BigDecimal("50000")));
    }

    @Test
    void consumeRejectsEventWithoutName() {
        AdvisorEventConsumer consumer = new AdvisorEventConsumer(advisorService);

        assertThrows(IllegalArgumentException.class,
                () -> consumer.consume(new AdvisorEvent("ADV-1", " ", null, AdvisorStatus.ACTIVE, null)));
        verifyNoInteractions(advisorService);
    }
}
