package com.consumersample.demo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class AppConfig {

    @Value(value = "${consumer.channel.prefetchCount}")
    private int prefetchCount;

    @Bean
    public ConnectionFactory connectionFactory() {
        final var factory = new ConnectionFactory();

        factory.setHost("localhost");
        factory.setUsername("user");
        factory.setPassword("password");
        factory.setVirtualHost("my_vhost");
        factory.setPort(5672);
        factory.setConnectionTimeout(3000);
        factory.setAutomaticRecoveryEnabled(true); //
        factory.setTopologyRecoveryEnabled(true); //

        return factory;
    }

    @Bean
    public Channel subscribeToQueue(final ConnectionFactory factory)
            throws IOException, TimeoutException {
        // try-with resources isn't used here, since we don't want the connection to be closed
        final var channel = factory.newConnection().createChannel();

        channel.basicQos(prefetchCount);

        return channel;
    }
}
