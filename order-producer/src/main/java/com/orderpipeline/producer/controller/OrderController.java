package com.orderpipeline.producer.controller;

import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderStatus;
import com.orderpipeline.producer.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderProducer orderProducer;
    
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        log.info("Received order from customer [{}]", order.getCustomer().getEmail());
        
        order.setId((long) (Math.random() * Integer.MAX_VALUE));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(order.calculateTotal());
        
        orderProducer.publishOrder(order);
        
        return ResponseEntity.accepted().body(order);
    }
}
