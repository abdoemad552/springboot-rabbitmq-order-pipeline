package com.orderpipeline.producer.controller;

import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.producer.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;
    
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        log.info("Received order from customer [{}]", order.getId());
        
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(order.calculateTotal());
        
        orderProducer.publishOrder(order);
        
        return ResponseEntity.accepted().body(order);
    }
}
