package com.orderpipeline.consumer.publisher;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.common.model.StatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    public void publish(Long orderId, OrderStatus status, String message) {
        StatusUpdate statusUpdate = StatusUpdate.of(orderId, status, message);
        log.info("Publishing status update for order [{}] — status: {}", orderId, status);
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.ORDER_EXCHANGE,
            RabbitMQConstants.STATUS_RK,
            statusUpdate
        );
    }
}
