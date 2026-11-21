package org.example.orderprocessing.messaging;

import org.example.orderprocessing.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final OrderService orderService;

    public OrderEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "${app.kafka.order-topic}",
            properties = "spring.json.value.default.type=org.example.orderprocessing.messaging.OrderEvent"
    )
    public void consume(OrderEvent event) {
        validate(event);
        log.info("Received OrderEvent: orderId={}, advisorId={}, status={}",
                event.orderId(), event.advisorId(), event.status());
        orderService.upsert(event.orderId(), event.toDetails());
    }

    /** Invalid events throw IllegalArgumentException, which the error handler logs and skips without retrying. */
    private static void validate(OrderEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("OrderEvent payload is empty");
        }
        if (isBlank(event.orderId()) || isBlank(event.advisorId()) || isBlank(event.customerId())
                || event.amount() == null || event.orderDate() == null) {
            throw new IllegalArgumentException(
                    "OrderEvent requires orderId, advisorId, customerId, amount and orderDate: orderId=" + event.orderId());
        }
        if (event.amount().signum() <= 0) {
            throw new IllegalArgumentException("OrderEvent amount must be positive: orderId=" + event.orderId());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
