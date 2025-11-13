package com.example.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    private static final Logger logger  = LoggerFactory.getLogger(MessageListener.class);
    @RabbitListener(queues = "${app.queue}")
    public void receive(String message) {
        logger.info("Received message: {}", message);
    }
}
