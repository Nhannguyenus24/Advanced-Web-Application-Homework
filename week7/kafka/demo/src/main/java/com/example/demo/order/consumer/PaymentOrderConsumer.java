package com.example.demo.order.consumer;

import com.example.demo.order.OrderEvent;
import com.example.demo.order.service.OrderEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentOrderConsumer.class);
    public static final String SERVICE_NAME = "payment-service";

    private final OrderEventStore eventStore;

    public PaymentOrderConsumer(OrderEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @KafkaListener(topics = "${app.kafka.order-topic:order-events}", groupId = SERVICE_NAME)
    public void handleOrderEvent(OrderEvent event) {
        log.info("Payment service xử lý order {} với trạng thái {}", event.orderId(), event.status());
        eventStore.recordEvent(SERVICE_NAME, event);
    }
}
