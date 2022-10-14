package com.consumersample.demo.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

    private static final String QUEUE_NAME = "events-queue";

    private static final String DEFAULT_EXCHANGE = "";

    private static final String EXCHANGE = DEFAULT_EXCHANGE;

    private static final List<String> consumedMessages = new ArrayList<>();

    @Resource
    private Channel channel;

    @Value(value = "${consumer.processingTimeInSeconds}")
    private long processingTimeInSeconds;

    public List<String> fetchMessages() {
        LOG.info("Fetching messages. List size [{}]", consumedMessages.size());
        return consumedMessages;
    }

    public DeliverCallback onMessageReceived() {
        return (consumerTag, delivery) -> {
            final var message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            final var deliveryTag = delivery.getEnvelope().getDeliveryTag();

            LOG.info("Received message [{}] - delivery tag [{}]", message, deliveryTag);
            try {
                LOG.info("Processing estimated time... [{}] seconds", processingTimeInSeconds);
                Thread.sleep(processingTimeInSeconds*1000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }

            consumedMessages.add(message);
            channel.basicAck(deliveryTag, false);
            LOG.info("Message processed with deliveryTag [{}]", deliveryTag);
        };
    }

    @PostConstruct
    public void subscribeToQueue() throws IOException {
        // durable: should the queue survive a server restart?
        // exclusive: queue should be exclusive to this connection?
        // autoDelete: will be removed by server, when no longer in use
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicConsume(QUEUE_NAME, false, this.onMessageReceived(), consumerTag -> {});

    }
}
