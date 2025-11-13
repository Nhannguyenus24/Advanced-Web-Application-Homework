package com.example.demo.order.service;

import com.example.demo.order.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String orderTopic;

    public OrderProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.order-topic:order-events}") String orderTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderTopic = orderTopic;
    }

    public void publish(OrderEvent event) {
        log.debug("Gửi order event tới topic {}: {}", orderTopic, event);
        kafkaTemplate.send(orderTopic, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Gửi order event thất bại", ex);
                    } else if (result != null) {
                        log.info("Gửi order event thành công tới partition {} với offset {}", result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
