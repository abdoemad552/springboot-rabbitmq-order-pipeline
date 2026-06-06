package com.orderpipeline.consumer.consumer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.InventoryResult;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.consumer.publisher.StatusPublisher;
import com.orderpipeline.consumer.service.InventoryService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {
    
    private final StatusPublisher statusPublisher;
    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = RabbitMQConstants.INVENTORY_QUEUE)
    public void onInventory(
        Order order,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        log.info("Received order [{}] on inventory queue", order.getId());
        statusPublisher.publish(order.getId(), OrderStatus.INVENTORY_PROCESSING, "Inventory reservation started");
        
        InventoryResult result = inventoryService.process(order);
        
        if (result.isSuccess()) {
            statusPublisher.publish(order.getId(), OrderStatus.INVENTORY_SUCCESS, "Inventory reserved");
            rabbitTemplate.convertAndSend(
                RabbitMQConstants.ORDER_EXCHANGE,
                RabbitMQConstants.SHIPPING_RK,
                order
            );
            channel.basicAck(deliveryTag, false);
        } else {
            statusPublisher.publish(order.getId(), OrderStatus.INVENTORY_FAILED, result.getFailureReason());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
