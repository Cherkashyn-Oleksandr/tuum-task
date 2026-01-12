package com.tuum.tuumtask.service;

import com.tuum.tuumtask.config.RabbitConfig;
import com.tuum.tuumtask.dto.AccountEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    private final RabbitTemplate rabbitTemplate;

    public PublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String routingKey, AccountEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.ACCOUNT_EXCHANGE,
                routingKey,
                event
        );
    }
}
