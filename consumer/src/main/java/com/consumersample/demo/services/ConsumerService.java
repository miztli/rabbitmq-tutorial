package com.consumersample.demo.services;

import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

    private static final List<String> consumedMessages = new ArrayList<>();

    public List<String> fetchMessages() {
        LOG.info("Fetching messages. List size [{}]", consumedMessages.size());
        return consumedMessages;
    }

    public DeliverCallback onMessageReceived() {
        return (consumerTag, delivery) -> {
            final var message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            LOG.info("Received message [{}]", message);
            consumedMessages.add(message);
        };
    }
}
