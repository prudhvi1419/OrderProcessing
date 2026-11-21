package org.example.orderprocessing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfiguration {
    /** Business clock; decides which month is "current" for commission periods. */
    @Bean
    Clock clock(@Value("${app.time-zone}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
