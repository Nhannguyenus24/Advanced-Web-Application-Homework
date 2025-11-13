package com.example.demo.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderEvent(
        String orderId,
        String customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        OffsetDateTime createdAt
) {
}
