package org.example.orderprocessing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Creates the inbound topics if missing (useful locally; in shared environments the upstream system owns them). */
@Configuration
public class KafkaTopicConfiguration {
    @Bean
    NewTopic orderTopic(@Value("${app.kafka.order-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic advisorTopic(@Value("${app.kafka.advisor-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
}
