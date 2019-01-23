package com.orderservice.config;

import com.orderservice.service.CreditMsgReceiver;
import common.AbstractStockAppRabbitConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;


@Configuration
public class RabbitOrderConfig extends AbstractStockAppRabbitConfiguration {
//
//    @Value("${stocks.quote.pattern}")
//    private String orderRoutingKey;
    Logger logger = LoggerFactory.getLogger(RabbitOrderConfig.class);

    @Override
    protected void configureRabbitTemplate(RabbitTemplate template) {
        template.setExchange(ORDER_CREATE_EXCHANGE_NAME);
        template.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) ->{
            logger.info("publisher confirmations: " + ack);
        });
    }

    //below is receiver side for credit
    @Bean
    public DirectExchange creditExchange() {
        return new DirectExchange(CREDIT_EXCHANGE_NAME);
    }

    @Bean
    Queue creditQueue() {
        System.out.println("creditQueue");
        return amqpAdmin().declareQueue();
    }

    @Bean
    Binding creditBinding() {
        return BindingBuilder.bind(creditQueue()).to(creditExchange()).with(CREDIT_RESULT_QUEUE);
    }

    //set listener Container for event Order-Created
    @Bean
    DirectMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        DirectMessageListenerContainer listenerContainer = new DirectMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setQueues(creditQueue());
        listenerContainer.setMessageListener(messageListenerAdapter);
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return listenerContainer;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(CreditMsgReceiver creditMsgReceiver) {
        MessageListenerAdapter listenerAdapter = new  MessageListenerAdapter(creditMsgReceiver);
        listenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
        listenerAdapter.setDefaultListenerMethod("messageHandler");
        return listenerAdapter;
    }

    @Bean
    public AmqpAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
