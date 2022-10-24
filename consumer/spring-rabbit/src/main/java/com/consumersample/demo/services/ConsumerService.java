package com.consumersample.demo.services;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @RabbitListener(queues = "cache-events-queue")
    public void onMessage(final String message,
                          final Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        LOG.info("Processing message in listener: [{}]", message);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        channel.basicAck(tag, false);
        LOG.info("Message [{}] was processed successfully and acknowledge", message);
    }
}
