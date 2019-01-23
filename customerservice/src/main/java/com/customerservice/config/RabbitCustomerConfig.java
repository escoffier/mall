package com.customerservice.config;

import com.customerservice.service.OrderMsgReceiver;
import common.AbstractStockAppRabbitConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCustomerConfig extends AbstractStockAppRabbitConfiguration {

    //used to send credit message
    @Override
    protected void configureRabbitTemplate(RabbitTemplate template) {
        template.setExchange(CREDIT_EXCHANGE_NAME);
        template.setRoutingKey(CREDIT_RESULT_QUEUE);
    }

    @Value("${mall.order.pattern}")
    private String orderRoutingKey;

    //below is receiver side for order message
    @Bean
    Queue orderQueue() {
        return amqpAdmin().declareQueue();
    }

    @Bean
    Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(orderRoutingKey);
    }


    //set listener Container for event Order-Created
    @Bean
    DirectMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        DirectMessageListenerContainer listenerContainer = new DirectMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setQueues(orderQueue());
        listenerContainer.setMessageListener(messageListenerAdapter);
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return listenerContainer;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(OrderMsgReceiver orderMsgReceiver) {
        MessageListenerAdapter listenerAdapter = new  MessageListenerAdapter(orderMsgReceiver);
        listenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
        listenerAdapter.setDefaultListenerMethod("receiveMessage");
        return listenerAdapter;
    }

    @Bean
    public AmqpAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
