package com.consumersample.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AppConfig {

    private static final String EXCHANGE = "events-exchange";

    @Value(value = "${rabbitmq.queue.x-expires-in-millis}")
    private long xExpiresInMillis;

    @Value(value = "${rabbitmq.queue.x-message-ttl-in-millis}")
    private long xMessageTtl;

    @Value(value = "${rabbitmq.queue.x-max-length}")
    private int xMaxLength;

    private Queue cacheEventsQueue() {
        final boolean isDurableQueue = false; // durable: should the queue survive a server restart?
        final boolean isChannelExclusive = false; // exclusive: queue should be exclusive to this connection?
        final boolean autoDeleteWhenDisconnected = false; // autoDelete: will be removed by server, when no longer in use
        /**
         * x-expires: How long a queue can be unused for before it is automatically deleted (milliseconds).
         * x-message-ttl: How long a message published to a queue can live before it is discarded (milliseconds).
         * x-overflow: Sets the queue overflow behaviour. This determines what happens to messages when the maximum length of a queue is reached. Valid values are drop-head, reject-publish or reject-publish-dlx. The quorum queue type only supports drop-head and reject-publish.
         * x-max-length: How many (ready) messages a queue can contain before it starts to drop them from its head.
         */
        final Map<String, Object> arguments = Map.of(
                "x-expires", xExpiresInMillis,
                "x-message-ttl", xMessageTtl,
                "x-max-length", xMaxLength);

        return new Queue("cache-events-queue", isDurableQueue, isChannelExclusive, autoDeleteWhenDisconnected, arguments);
    }

    private FanoutExchange fanoutExchange() {
        final var isDurable = false;
        final var autoDelete = false;
        return new FanoutExchange(EXCHANGE, isDurable, autoDelete);
    }

    @Bean
    public Declarables declarables() {
        final var cacheEventsQueue = cacheEventsQueue();
        final var fanoutExchange = fanoutExchange();
        final var bindings = BindingBuilder.bind(cacheEventsQueue).to(fanoutExchange);

        return new Declarables(
                cacheEventsQueue,
                fanoutExchange,
                bindings);
    }
}
