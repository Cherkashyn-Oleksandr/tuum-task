package com.tuum.tuumtask.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ACCOUNT_EXCHANGE = "account.exchange";

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(ACCOUNT_EXCHANGE);
    }
}
