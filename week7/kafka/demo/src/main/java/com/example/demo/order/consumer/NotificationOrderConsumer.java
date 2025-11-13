package com.example.demo.order.consumer;

import com.example.demo.order.OrderEvent;
import com.example.demo.order.service.OrderEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationOrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationOrderConsumer.class);
    public static final String SERVICE_NAME = "notification-service";

    private final OrderEventStore eventStore;

    public NotificationOrderConsumer(OrderEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @KafkaListener(topics = "${app.kafka.order-topic:order-events}", groupId = SERVICE_NAME)
    public void handleOrderEvent(OrderEvent event) {
        log.info("Notification service gửi thông báo cho order {}", event.orderId());
        eventStore.recordEvent(SERVICE_NAME, event);
    }
}
