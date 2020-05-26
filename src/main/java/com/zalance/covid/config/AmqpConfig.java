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
    @Value("${zalance.covid.country.queue.name}")
    private String covidCountryQueueName;
    @Value("${zalance.covid.cases.queue.name}")
    private String covidCasesQueueName;

    @Value("${zalance.covid.country.retry.queue.name}")
    private String covidCountryRetryQueueName;
    @Value("${zalance.covid.cases.retry.queue.name}")
    private String covidCasesRetryQueueName;

    @Value("${zalance.covid.x-msg-ttl.name}")
    private int xMsgTtl;

    @Value("${zalance.covid.exchange.name}")
    private String covidDirectExchangeName;
    @Value("${zalance.covid.deadletter.exchange.name}")
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
                .withArgument("x-message-ttl", xMsgTtl * 60 * 1000)
                .build();
    }

    @Bean
    Queue covidCountryRetryQueue() {
        return QueueBuilder.durable(covidCountryRetryQueueName)
                .deadLetterExchange(covidDirectExchangeName)
                .withArgument("x-message-ttl", xMsgTtl * 60 * 1000)
                .build();
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(covidDirectExchangeName);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(covidDeadLetterExchangeName);
    }

    @Bean
    Binding covidCasesBinding(@Qualifier("covidCasesQueue") Queue queue, @Qualifier("exchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(covidCasesQueueName);
    }

    @Bean
    Binding covidCountryBinding(@Qualifier("covidCountryQueue") Queue queue, @Qualifier("exchange") DirectExchange exchange) {
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
    public SimpleRabbitListenerContainerFactory myRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
