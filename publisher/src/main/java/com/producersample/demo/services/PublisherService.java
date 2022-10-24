package com.producersample.demo.services;

import com.producersample.demo.dto.Message;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Service
public class PublisherService {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherService.class);

    private static final String IGNORED_QUEUE_NAME = "ignored-queue-name";

    private static final String DEFAULT_EXCHANGE = "";

    private static final String EXCHANGE = "events-exchange";

    @Resource
    private Channel channel;

    public void publishMessage(final Message message) throws IOException {
        for (int i = 0; i < message.getTimes(); i++) {
            final var text = String.format("%s [%s]", message.getMessage(), i);
            channel.basicPublish(EXCHANGE, IGNORED_QUEUE_NAME, null, text.getBytes());
            LOG.info("Message [{}] sent to exchange [{}] - queue [{}]", text, EXCHANGE, IGNORED_QUEUE_NAME);
        }
    }

    @PostConstruct
    public void declareExchange() throws IOException {
        LOG.info("Declaring exchange name: [{}] of type: [{}]", EXCHANGE, BuiltinExchangeType.FANOUT.getType());
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);
    }
}
