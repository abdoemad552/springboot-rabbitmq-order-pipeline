package com.orderpipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    
    private Long orderId;
    private boolean success;
    private String transactionId;
    private BigDecimal chargedAmount;
    private String failureReason;
}
