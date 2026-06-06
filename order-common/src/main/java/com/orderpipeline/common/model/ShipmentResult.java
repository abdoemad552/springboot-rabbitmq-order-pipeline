package com.orderpipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResult {
    
    private Long orderId;
    private boolean success;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private String failureReason;
}
