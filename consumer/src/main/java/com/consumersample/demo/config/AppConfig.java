package com.consumersample.demo.config;

import com.consumersample.demo.services.ConsumerService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class AppConfig {

    private static final String QUEUE_NAME = "events-queue";

    private static final String DEFAULT_EXCHANGE = "";

    private static final String EXCHANGE = DEFAULT_EXCHANGE;

    @Bean
    public ConnectionFactory connectionFactory() {
        final var factory = new ConnectionFactory();

        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        factory.setConnectionTimeout(3000);
        factory.setAutomaticRecoveryEnabled(true); //
        factory.setTopologyRecoveryEnabled(true); //

        return factory;
    }

    @Bean
    public Channel subscribeToQueue(final ConnectionFactory factory, final ConsumerService consumerService)
            throws IOException, TimeoutException {
        // try-with resources isn't used here, since we don't want the connection to be closed
        final var channel = factory.newConnection().createChannel();

        // durable: should the queue survive a server restart?
        // exclusive: queue should be exclusive to this connection?
        // autoDelete: will be removed by server, when no longer in use
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicConsume(QUEUE_NAME, true, consumerService.onMessageReceived(), consumerTag -> {});

        return channel;
    }
}
