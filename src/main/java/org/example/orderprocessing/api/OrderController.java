package org.example.orderprocessing.api;

import jakarta.validation.Valid;
import org.example.orderprocessing.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders received: orderId={}, advisorId={}", request.orderId(), request.advisorId());
        OrderResponse response = OrderResponse.from(orderService.create(request.orderId(), request.toDetails()));
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.orderId())
                        .toUri()
        ).body(response);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable String id, @Valid @RequestBody UpdateOrderRequest request) {
        log.info("PUT /api/orders/{} received", id);
        return OrderResponse.from(orderService.update(id, request.toDetails()));
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable String id) {
        log.debug("GET /api/orders/{} received", id);
        return OrderResponse.from(orderService.findById(id));
    }

    /** GET /api/orders?advisorId=ADV-1[&period=2026-11] */
    @GetMapping
    public List<OrderResponse> findByAdvisor(
            @RequestParam String advisorId,
            @RequestParam(required = false) YearMonth period
    ) {
        log.debug("GET /api/orders received: advisorId={}, period={}", advisorId, period);
        return orderService.findByAdvisor(advisorId, period).stream().map(OrderResponse::from).toList();
    }
}
