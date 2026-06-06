package com.orderpipeline.consumer.consumer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.common.model.PaymentResult;
import com.orderpipeline.consumer.publisher.StatusPublisher;
import com.orderpipeline.consumer.service.PaymentService;
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
public class PaymentConsumer {

    private final StatusPublisher statusPublisher;
    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = RabbitMQConstants.PAYMENT_QUEUE)
    public void onPayment(
        Order order,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        log.info("Received order [{}] on payment queue", order.getId());
        statusPublisher.publish(order.getId(), OrderStatus.PAYMENT_PROCESSING, "Payment started");
        
        PaymentResult result = paymentService.process(order);
        
        if (result.isSuccess()) {
            statusPublisher.publish(order.getId(), OrderStatus.PAYMENT_SUCCESS, "Payment successful");
            rabbitTemplate.convertAndSend(
                RabbitMQConstants.ORDER_EXCHANGE,
                RabbitMQConstants.INVENTORY_RK,
                order
            );
            channel.basicAck(deliveryTag, false);
        } else {
            statusPublisher.publish(order.getId(), OrderStatus.PAYMENT_FAILED, result.getFailureReason());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
