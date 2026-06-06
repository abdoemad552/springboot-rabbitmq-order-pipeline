package com.orderpipeline.producer.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderpipeline.common.constant.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(RabbitMQConstants.ORDER_EXCHANGE);
    }
    
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(RabbitMQConstants.PAYMENT_QUEUE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_EXCHANGE, RabbitMQConstants.ORDER_EXCHANGE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_RK, RabbitMQConstants.DLQ_RK)
            .build();
    }
    
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(RabbitMQConstants.INVENTORY_QUEUE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_EXCHANGE, RabbitMQConstants.ORDER_EXCHANGE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_RK, RabbitMQConstants.DLQ_RK)
            .build();
    }
    
    @Bean
    public Queue shippingQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SHIPPING_QUEUE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_EXCHANGE, RabbitMQConstants.ORDER_EXCHANGE)
            .withArgument(RabbitMQConstants.DLQ_DEAD_LETTER_RK, RabbitMQConstants.DLQ_RK)
            .build();
    }
    
    @Bean
    public Queue statusQueue() {
        return QueueBuilder.durable(RabbitMQConstants.STATUS_QUEUE).build();
    }
    
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DLQ_QUEUE).build();
    }
    
    @Bean
    public Binding paymentBinding(Queue paymentQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(paymentQueue).to(orderExchange).with(RabbitMQConstants.PAYMENT_RK);
    }
    
    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(inventoryQueue).to(orderExchange).with(RabbitMQConstants.INVENTORY_RK);
    }
    
    @Bean
    public Binding shippingBinding(Queue shippingQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(shippingQueue).to(orderExchange).with(RabbitMQConstants.SHIPPING_QUEUE);
    }
    
    @Bean
    public Binding statusBinding(Queue statusQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(statusQueue).to(orderExchange).with(RabbitMQConstants.STATUS_RK);
    }
    
    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(dlqQueue).to(orderExchange).with(RabbitMQConstants.DLQ_RK);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
