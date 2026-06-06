package com.orderpipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResult {

    private Long orderId;
    private boolean success;
    private List<Long> reservedItems;
    private String failureReason;
}
