package com.orderpipeline.producer.websocket;

import com.orderpipeline.common.model.StatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusWebSocketBroker {

    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendStatusUpdate(StatusUpdate statusUpdate) {
        String destination = "/topic/orders/" + statusUpdate.getOrderId();
        log.info("Pushing status update to WebSocket [{}] — status: {}",
            destination, statusUpdate.getStatus());
        messagingTemplate.convertAndSend(destination, statusUpdate);
    }
}
