package com.example.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = "${app.queue}")
    public void receive(String message) {
        System.out.println("📩 Received message: " + message);
    }
}
