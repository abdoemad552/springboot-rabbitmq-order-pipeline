package com.orderpipeline.consumer.service;

import com.orderpipeline.common.model.InventoryResult;
import com.orderpipeline.common.model.Order;
import com.orderpipeline.common.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InventoryService {
    
    private final Map<Long, Integer> stock = new HashMap<>(Map.of(
        1L, 100,
        2L, 50,
        3L, 0
    ));
    
    public InventoryResult process(Order order) {
        log.info("Processing inventory for order [{}]", order.getId());
        
        try {
            Thread.sleep(1000);
            
            for (var item : order.getItems()) {
                Long productId = item.getProduct().getId();
                Integer available = stock.getOrDefault(productId, 0);

                if (item.getQuantity() > available) {
                    return InventoryResult.builder()
                        .orderId(order.getId())
                        .success(false)
                        .failureReason("Insufficient stock for product: " + productId)
                        .build();
                }
            }
            
            List<Long> reservedIds = order.getItems().stream()
                .peek(item -> stock.merge(item.getProduct().getId(), -item.getQuantity(), Integer::sum))
                .map(item -> item.getProduct().getId())
                .toList();
            
            return InventoryResult.builder()
                .orderId(order.getId())
                .success(true)
                .reservedItems(reservedIds)
                .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Inventory processing interrupted");
        }
    }
}
