package com.orderpipeline.consumer.consumer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.consumer.publisher.StatusPublisher;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterConsumer {
    
    private final StatusPublisher statusPublisher;
    
    @RabbitListener(queues = RabbitMQConstants.DLQ_QUEUE)
    public void onDeadLetter(
        Order order,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        log.error("Order [{}] landed in DLQ at status [{}] — triggering compensation",
            order.getId(), order.getStatus());
        
        switch (order.getStatus()) {
            case PAYMENT_FAILED ->
                statusPublisher.publish(order.getId(), OrderStatus.FAILED, "Order failed — payment unsuccessful");
            case INVENTORY_FAILED ->
                statusPublisher.publish(order.getId(), OrderStatus.FAILED, "OOrder failed — inventory unavailable, payment will be refunded");
            case SHIPPING_FAILED ->
                statusPublisher.publish(order.getId(), OrderStatus.FAILED, "Order failed — shipment error, payment and inventory will be released");
            default ->
                statusPublisher.publish(order.getId(), OrderStatus.FAILED, "Order failed — unknown error");
        }
        
        channel.basicAck(deliveryTag, false);
    }
}
