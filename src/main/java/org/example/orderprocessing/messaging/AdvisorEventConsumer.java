package org.example.orderprocessing.messaging;

import org.example.orderprocessing.service.AdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AdvisorEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AdvisorEventConsumer.class);

    private final AdvisorService advisorService;

    public AdvisorEventConsumer(AdvisorService advisorService) {
        this.advisorService = advisorService;
    }

    @KafkaListener(
            topics = "${app.kafka.advisor-topic}",
            properties = "spring.json.value.default.type=org.example.orderprocessing.messaging.AdvisorEvent"
    )
    public void consume(AdvisorEvent event) {
        validate(event);
        log.info("Received AdvisorEvent: advisorId={}, status={}", event.advisorId(), event.status());
        advisorService.upsert(event.advisorId(), event.toDetails());
    }

    /** Invalid events throw IllegalArgumentException, which the error handler logs and skips without retrying. */
    private static void validate(AdvisorEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("AdvisorEvent payload is empty");
        }
        if (isBlank(event.advisorId()) || isBlank(event.name())) {
            throw new IllegalArgumentException("AdvisorEvent requires advisorId and name: advisorId=" + event.advisorId());
        }
        if (event.monthlyTarget() != null && event.monthlyTarget().signum() < 0) {
            throw new IllegalArgumentException("AdvisorEvent monthlyTarget must not be negative: advisorId=" + event.advisorId());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
