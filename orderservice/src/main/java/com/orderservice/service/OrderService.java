package com.orderservice.service;

import common.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public Order postOrder(Long id, Order order) {

        String routingKey = "mall.order.created";
        rabbitTemplate.convertAndSend(routingKey, order);
        return order;
    }
}
