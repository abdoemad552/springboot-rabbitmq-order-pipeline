package com.orderpipeline.producer.consumer;

import com.orderpipeline.common.constant.RabbitMQConstants;
import com.orderpipeline.common.model.StatusUpdate;
import com.orderpipeline.producer.websocket.StatusWebSocketBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusConsumer {

    private final StatusWebSocketBroker statusWebSocketBroker;
    
    @RabbitListener(queues = RabbitMQConstants.STATUS_QUEUE)
    public void onStatusUpdate(StatusUpdate statusUpdate) {
        statusWebSocketBroker.sendStatusUpdate(statusUpdate);
    }
}
