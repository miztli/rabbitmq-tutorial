package com.consumersample.demo.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(final String message) {
        rabbitTemplate.convertAndSend("cache-events-queue", message);
    }
}
