package com.orderpipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdate {

    private Long orderId;
    private OrderStatus status;
    private String message;
    private LocalDateTime timestamp;
    
    public static StatusUpdate of(Long orderId, OrderStatus status, String message) {
        return StatusUpdate.builder()
            .orderId(orderId)
            .status(status)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
