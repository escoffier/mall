package com.customerservice.service;

import common.model.Credit;
import common.model.Order;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMsgReceiver {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void receiveMessage(String message) {
        System.out.println("OrderMsgReceiver Received <" + message + ">  -- " + Thread.currentThread().getName());
    }

    public void receiveMessage(Order order) {
        System.out.println("OrderMsgReceiver Received <" + order.toString() + ">  -- " + Thread.currentThread().getName());
        Credit credit = new Credit();
        credit.setStatus("Reserved");
        rabbitTemplate.convertAndSend(credit, m -> {
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });
    }
}
