package com.example.demo.order.service;

import com.example.demo.order.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderEventReplayService {

    private static final Logger log = LoggerFactory.getLogger(OrderEventReplayService.class);
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(500);

    private final ConsumerFactory<String, Object> consumerFactory;
    private final String orderTopic;
    private final ObjectMapper objectMapper;

    public OrderEventReplayService(
            ConsumerFactory<String, Object> consumerFactory,
            @Value("${app.kafka.order-topic:order-events}") String orderTopic,
            ObjectMapper objectMapper
    ) {
        this.consumerFactory = consumerFactory;
        this.orderTopic = orderTopic;
        this.objectMapper = objectMapper;
    }

    public List<OrderEvent> replayAllEvents() {
        String replayGroupId = "order-replay-" + UUID.randomUUID();
        log.info("Bắt đầu replay events cho topic {} với groupId {}", orderTopic, replayGroupId);

        List<OrderEvent> events = new ArrayList<>();

        try (Consumer<String, Object> consumer = consumerFactory.createConsumer(replayGroupId, "replay", null)) {
            List<TopicPartition> partitions = fetchPartitions(consumer);
            if (partitions.isEmpty()) {
                log.warn("Không thể tìm thấy partition nào của topic {}", orderTopic);
                return events;
            }

            log.debug("Replay assign partitions: {}", partitions);
            consumer.assign(partitions);
            consumer.seekToBeginning(partitions);

            ConsumerRecords<String, Object> records;
            int emptyPolls = 0;

            while (true) {
                records = consumer.poll(POLL_TIMEOUT);

                if (records.isEmpty()) {
                    if (++emptyPolls >= 3) {
                        break;
                    }
                    continue;
                }

                emptyPolls = 0;

                for (ConsumerRecord<String, Object> record : records) {
                    OrderEvent event = convert(record.value());
                    if (event != null) {
                        events.add(event);
                    }
                }
            }
        }

        log.info("Replay hoàn tất, đọc được {} events", events.size());
        return events;
    }

    private List<TopicPartition> fetchPartitions(Consumer<String, Object> consumer) {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(orderTopic, Duration.ofSeconds(5));
        if (partitionInfos == null || partitionInfos.isEmpty()) {
            return List.of();
        }
        return partitionInfos.stream()
                .map(info -> new TopicPartition(info.topic(), info.partition()))
                .collect(Collectors.toList());
    }

    private OrderEvent convert(Object value) {
        if (value instanceof OrderEvent event) {
            return event;
        }
        try {
            return objectMapper.convertValue(value, OrderEvent.class);
        } catch (IllegalArgumentException ex) {
            log.warn("Không thể chuyển đổi record {} sang OrderEvent", value, ex);
            return null;
        }
    }
}
