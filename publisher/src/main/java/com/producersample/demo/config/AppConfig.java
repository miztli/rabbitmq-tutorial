package com.producersample.demo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class AppConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        final var factory = new ConnectionFactory();

        factory.setHost("localhost");
        factory.setUsername("user");
        factory.setPassword("password");
        factory.setVirtualHost("my_vhost");
        factory.setPort(5672);
        factory.setConnectionTimeout(3000);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);

        return factory;
    }

    @Bean
    public Channel publisherChannel(final ConnectionFactory factory) throws IOException, TimeoutException {
        return factory.newConnection().createChannel();
    }
}
