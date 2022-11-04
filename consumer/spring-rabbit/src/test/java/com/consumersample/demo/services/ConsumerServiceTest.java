package com.consumersample.demo.services;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

/**
 * What are we trying to achieve?
 * Test that after sending a message, the listener receives the message as expected.
 */
@SpringJUnitConfig(classes = {
        ConsumerServiceTest.Config.class,
        ConsumerService.class,
        ProducerService.class
})
/**
 * Use this annotation to add infrastructure beans to the Spring test ApplicationContext.
 * This is not necessary when using, for example @SpringBootTest since Spring Boot’s auto configuration will add the beans.
 * - CachingConnectionFactory (autoConnectionFactory). If @RabbitEnabled is present, its connection factory is used.
 * - RabbitTemplate (autoRabbitTemplate)
 * - RabbitAdmin (autoRabbitAdmin)
 * - RabbitListenerContainerFactory (autoContainerFactory)
 * NOTE: Use this annotation for integration testing with real rabbitMq instance
 */
//@SpringRabbitTest
@TestPropertySource("classpath:application-test.properties")
class ConsumerServiceTest {

    @Autowired
    private Config config;

    @Autowired
    private ProducerService producerService;

    @Test
    public void testRabbitThroughTestRabbitTemplate() {
        producerService.sendMessage("Hello miztli!");
    }

    @Configuration
    @EnableRabbit
    public static class Config {

        @Bean
        public TestRabbitTemplate template() throws IOException {
            final TestRabbitTemplate template = new TestRabbitTemplate(connectionFactory());

            template.addBeforePublishPostProcessors(message -> {
                message.getMessageProperties().setDeliveryTag(1);
                return message;
            });

            return template;
        }

        @Bean
        public ConnectionFactory connectionFactory() throws IOException {
            ConnectionFactory factory = mock(ConnectionFactory.class);
            Connection connection = mock(Connection.class);
            Channel channel = mock(Channel.class);
            willReturn(connection).given(factory).createConnection();
            willReturn(channel).given(connection).createChannel(anyBoolean());
            willReturn("tag").given(channel).basicConsume(anyString(), any());
            given(channel.isOpen()).willReturn(true);
            return factory;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() throws IOException {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory());
            factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
            factory.setPrefetchCount(1);

            return factory;
        }

        @Bean
        public SimpleMessageListenerContainer simpleMessageListenerContainer() throws IOException {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
            container.setQueueNames("cache-events-queue");
            return container;
        }

        @Bean Queue testQueue() {
            return new Queue("");
        }
    }
}