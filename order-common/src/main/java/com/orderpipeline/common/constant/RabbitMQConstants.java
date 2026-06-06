package com.orderpipeline.common.constant;

public class RabbitMQConstants {

    public static final String ORDER_EXCHANGE  = "order.exchange";
    
    public static final String PAYMENT_QUEUE   = "payment.queue";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String SHIPPING_QUEUE  = "shipping.queue";
    public static final String STATUS_QUEUE    = "status.queue";
    public static final String DLQ_QUEUE       = "dlq.queue";
    
    public static final String PAYMENT_RK      = "order.payment";
    public static final String INVENTORY_RK    = "order.inventory";
    public static final String SHIPPING_RK     = "order.shipping";
    public static final String STATUS_RK       = "order.status";
    public static final String DLQ_RK          = "order.dlq";
    
    public static final String DLQ_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String DLQ_DEAD_LETTER_RK       = "x-dead-letter-routing-key";
}
