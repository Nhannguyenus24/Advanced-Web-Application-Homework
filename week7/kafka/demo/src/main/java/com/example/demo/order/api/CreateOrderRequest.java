package com.example.demo.order.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "customerId không được để trống")
        String customerId,

        @NotNull(message = "totalAmount không được null")
        @Positive(message = "totalAmount phải lớn hơn 0")
        BigDecimal totalAmount
) {
}
