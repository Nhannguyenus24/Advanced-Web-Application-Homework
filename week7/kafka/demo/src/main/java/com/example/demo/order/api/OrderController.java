package com.example.demo.order.api;

import com.example.demo.order.OrderEvent;
import com.example.demo.order.OrderStatus;
import com.example.demo.order.consumer.InventoryOrderConsumer;
import com.example.demo.order.consumer.NotificationOrderConsumer;
import com.example.demo.order.consumer.PaymentOrderConsumer;
import com.example.demo.order.service.OrderEventReplayService;
import com.example.demo.order.service.OrderEventStore;
import com.example.demo.order.service.OrderProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    static final List<String> SERVICE_NAMES = List.of(
            PaymentOrderConsumer.SERVICE_NAME,
            InventoryOrderConsumer.SERVICE_NAME,
            NotificationOrderConsumer.SERVICE_NAME
    );

    private final OrderProducer orderProducer;
    private final OrderEventStore eventStore;
    private final OrderEventReplayService replayService;

    public OrderController(OrderProducer orderProducer,
                           OrderEventStore eventStore,
                           OrderEventReplayService replayService) {
        this.orderProducer = orderProducer;
        this.eventStore = eventStore;
        this.replayService = replayService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.totalAmount(),
                OrderStatus.CREATED,
                OffsetDateTime.now()
        );

        orderProducer.publish(event);
        log.info("Đã gửi order event: {}", event);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "message", "Order event đã được gửi",
                        "orderId", event.orderId()
                ));
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Map<String, List<OrderEvent>> eventsByService = SERVICE_NAMES.stream()
                .collect(Collectors.toMap(
                        service -> service,
                        eventStore::getEvents
                ));

        Map<String, Integer> counts = eventsByService.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()));

        return ResponseEntity.ok(Map.of(
                "services", SERVICE_NAMES,
                "counts", counts,
                "events", eventsByService
        ));
    }

    @GetMapping("/events/{serviceName}")
    public ResponseEntity<Map<String, Object>> getEventsByService(@PathVariable String serviceName) {
        String normalized = serviceName.toLowerCase(Locale.ROOT);
        String matchedService = SERVICE_NAMES.stream()
                .filter(name -> name.equalsIgnoreCase(normalized))
                .findFirst()
                .orElse(null);

        if (matchedService == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Service không tồn tại",
                            "supported", SERVICE_NAMES
                    ));
        }

        List<OrderEvent> events = eventStore.getEvents(matchedService);
        return ResponseEntity.ok(Map.of(
                "service", matchedService,
                "count", events.size(),
                "events", events
        ));
    }

    @PostMapping("/events/replay")
    public ResponseEntity<Map<String, Object>> replayEvents() {
        List<OrderEvent> events = replayService.replayAllEvents();
        SERVICE_NAMES.forEach(service -> eventStore.replaceEvents(service, events));
        return ResponseEntity.ok(Map.of(
                "message", "Đã replay các order events",
                "services", SERVICE_NAMES,
                "eventCount", events.size(),
                "events", events
        ));
    }
}
