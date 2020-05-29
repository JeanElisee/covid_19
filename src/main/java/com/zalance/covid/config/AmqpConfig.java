package com.zalance.covid.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.Serializable;

@Configuration
public class AmqpConfig implements Serializable {
    @Value("${zalance.queue.covid.name.country}")
    private String covidCountryQueueName;
    @Value("${zalance.queue.covid.name.cases}")
    private String covidCasesQueueName;

    @Value("${zalance.retry.queue.covid.name.country}")
    private String covidCountryRetryQueueName;
    @Value("${zalance.retry.queue.covid.name.cases}")
    private String covidCasesRetryQueueName;

    @Value("${zalance.notification.retry.x-msg-ttl}")
    private int xMsgTtl;

    @Value("${zalance.retry.exchange.name}")
    private String covidDirectExchangeName;
    @Value("${zalance.exchange.name}")
    private String covidDeadLetterExchangeName;

    @Primary
    @Bean
    Queue covidCasesQueue() {
        return QueueBuilder.durable(covidCasesQueueName)
                .deadLetterExchange(covidDeadLetterExchangeName)
                .build();
    }

    @Bean
    Queue covidCountryQueue() {
        return QueueBuilder.durable(covidCountryQueueName)
                .deadLetterExchange(covidDeadLetterExchangeName)
                .build();
    }

    @Bean
    Queue covidCasesRetryQueue() {
        return QueueBuilder.durable(covidCasesRetryQueueName)
                .deadLetterExchange(covidDirectExchangeName)
                .withArgument("x-message-ttl", 1000 * 60 * xMsgTtl)
                .build();
    }

    @Bean
    Queue covidCountryRetryQueue() {
        return QueueBuilder.durable(covidCountryRetryQueueName)
                .deadLetterExchange(covidDirectExchangeName)
                .withArgument("x-message-ttl", 1000 * 60 * xMsgTtl)
                .build();
    }

    @Bean
    DirectExchange covidExchange() {
        return new DirectExchange(covidDirectExchangeName);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(covidDeadLetterExchangeName);
    }

    @Bean
    Binding covidCasesBinding(@Qualifier("covidCasesQueue") Queue queue, @Qualifier("covidExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(covidCasesQueueName);
    }

    @Bean
    Binding covidCountryBinding(@Qualifier("covidCountryQueue") Queue queue, @Qualifier("covidExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(covidCountryQueueName);
    }

    @Bean
    Binding covidCasesRetryQueueBinding(@Qualifier("covidCasesRetryQueue") Queue queue, @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(covidCasesQueueName);
    }

    @Bean
    Binding covidCountryRetryBinding(@Qualifier("covidCountryRetryQueue") Queue queue, @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(covidCountryQueueName);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory covidRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
