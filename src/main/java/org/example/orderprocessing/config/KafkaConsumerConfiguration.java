package org.example.orderprocessing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    /**
     * Retries a failed record 3 times, 1s apart, then logs and skips it so one bad message cannot block the partition.
     * Invalid payloads (IllegalArgumentException) and undeserializable messages are skipped immediately.
     */
    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                (record, exception) -> log.error(
                        "Skipping Kafka record topic={} partition={} offset={} key={}",
                        record.topic(), record.partition(), record.offset(), record.key(), exception),
                new FixedBackOff(1000L, 3L)
        );
        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
}
