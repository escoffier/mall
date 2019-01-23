package common;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public abstract class AbstractStockAppRabbitConfiguration {
    protected static String ORDER_CREATE_EXCHANGE_NAME = "mall.orderExchange";//topic exchange的名稱

    //protected static String STOCK_REQUEST_QUEUE_NAME = "mall.orderRequest";

    //protected static String STOCK_REQUEST_ROUTING_KEY = STOCK_REQUEST_QUEUE_NAME;

    protected static String CREDIT_RESULT_QUEUE = "mall.credit";

    protected static String CREDIT_EXCHANGE_NAME = "mall.creditExchange";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("140.143.45.252");
        connectionFactory.setUsername("robbie");
        connectionFactory.setPassword("19811981");
        connectionFactory.setVirtualHost("mallHost");
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    protected abstract void configureRabbitTemplate(RabbitTemplate template);

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());

        //retry policy
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        rabbitTemplate.setRetryTemplate(retryTemplate);

        configureRabbitTemplate(rabbitTemplate);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_CREATE_EXCHANGE_NAME);
    }

    @Bean
    public RabbitAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
