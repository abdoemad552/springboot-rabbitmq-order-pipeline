package com.orderpipeline.consumer.service;

import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.ShipmentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class ShippingService {
    
    public ShipmentResult process(Order order) {
        log.info("Processing shipment for order [{}]", order.getId());
        
        try {
            Thread.sleep(1000);
            
            return ShipmentResult.builder()
                .orderId(order.getId())
                .success(true)
                .trackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .estimatedDelivery(LocalDateTime.now().plusDays(3))
                .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Shipping processing interrupted", e);
        }
    }
}
