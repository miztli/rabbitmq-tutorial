package com.consumersample.demo.services;

import com.rabbitmq.client.BuiltinExchangeType;
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
import java.util.UUID;

@Service
public class ConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);
    
    private static final String DEFAULT_EXCHANGE = "";

    private static final String EXCHANGE = "events-exchange";

    private static final List<String> consumedMessages = new ArrayList<>();

    @Resource
    private Channel channel;

    @Value(value = "${consumer.processingTimeInSeconds}")
    private long processingTimeInSeconds;

    @Value(value = "${spring.application.name}")
    private String appName;

    @Value(value = "${instanceId}")
    private String instanceId;

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
        // CREATE EXCHANGE
        LOG.info("Declaring exchange name: [{}] of type: [{}]", EXCHANGE, BuiltinExchangeType.FANOUT.getType());
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);

        // CREATE QUEUE
        final String queueName = resolveQueueName();
        LOG.info("Creating queue [{}]", queueName);
        final boolean isDurableQueue = false; // durable: should the queue survive a server restart?
        final boolean isChannelExclusive = false; // exclusive: queue should be exclusive to this connection?
        final boolean autoDeleteWhenDisconnected = false; // autoDelete: will be removed by server, when no longer in use
        channel.queueDeclare(queueName, isDurableQueue, isChannelExclusive, autoDeleteWhenDisconnected, null);

        // BIND QUEUE TO EXCHANGE
        final String routingKey = "";
        channel.queueBind(queueName, EXCHANGE, routingKey);

        // CONSUME MESSAGE
        channel.basicConsume(queueName, false, this.onMessageReceived(), consumerTag -> {});
        LOG.info("Starting to consume messages from queue [{}]", queueName);
    }

    private String resolveQueueName() {
        final String prefix = appName != null ? appName : "consumer-app";
        final String suffix = instanceId != "0" ? instanceId : UUID.randomUUID().toString();

        return prefix + "." + suffix;
    }
}
