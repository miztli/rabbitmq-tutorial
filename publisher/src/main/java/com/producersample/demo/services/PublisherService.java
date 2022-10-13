package com.producersample.demo.services;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class PublisherService {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherService.class);

    private static final String QUEUE_NAME = "events-queue";

    private static final String DEFAULT_EXCHANGE = "";

    private static final String EXCHANGE = DEFAULT_EXCHANGE;

    @Resource
    private Channel channel;

    public void publishMessage(final String message) throws IOException {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish(EXCHANGE, QUEUE_NAME, null, message.getBytes());
        LOG.info("Message [{}] sent to exchange [{}] - queue [{}]", message, DEFAULT_EXCHANGE, QUEUE_NAME);
    }
}
