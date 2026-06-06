package com.orderpipeline.consumer.consumer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.common.model.ShipmentResult;
import com.orderpipeline.consumer.publisher.StatusPublisher;
import com.orderpipeline.consumer.service.ShippingService;
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
public class ShippingConsumer {
    
    private final StatusPublisher statusPublisher;
    private final ShippingService shippingService;
    
    @RabbitListener(queues = RabbitMQConstants.SHIPPING_QUEUE)
    public void onShipping(
        Order order,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        log.info("Received order [{}] on shipping queue", order.getId());
        statusPublisher.publish(order.getId(), OrderStatus.SHIPPING_PROCESSING, "Shipment creation started");
        
        ShipmentResult result = shippingService.process(order);
        
        if (result.isSuccess()) {
            statusPublisher.publish(order.getId(), OrderStatus.COMPLETED, "Order completed — tracking: " + result.getTrackingNumber());
            channel.basicAck(deliveryTag, false);
        } else {
            statusPublisher.publish(order.getId(), OrderStatus.SHIPPING_FAILED, result.getFailureReason());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
