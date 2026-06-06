package com.orderpipeline.consumer.service;

import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    public PaymentResult process(Order order) {
        log.info("Processing payment for order [{}] — amount: {}",
            order.getId(), order.getTotalAmount());
        
        try {
            Thread.sleep(1000);
            
            if (Math.random() < 0.2) {
                throw new RuntimeException("Insufficient funds");
            }
            
            return PaymentResult.builder()
                .orderId(order.getId())
                .success(true)
                .transactionId(UUID.randomUUID().toString())
                .chargedAmount(order.getTotalAmount())
                .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted", e);
        } catch (RuntimeException e) {
            log.error("Payment failed for order [{}]: {}", order.getId(), e.getMessage());
            return PaymentResult.builder()
                .orderId(order.getId())
                .success(false)
                .failureReason(e.getMessage())
                .build();
        }
    }
}
