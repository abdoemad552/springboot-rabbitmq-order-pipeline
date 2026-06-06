package com.orderpipeline.producer.producer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;
    
    public void publishOrder(Order order) {
        order.setStatus(OrderStatus.PAYMENT_PROCESSING);
        log.info("Publishing order [{}] to payment queue", order.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.ORDER_EXCHANGE,
            RabbitMQConstants.PAYMENT_RK,
            order
        );
    }
}
