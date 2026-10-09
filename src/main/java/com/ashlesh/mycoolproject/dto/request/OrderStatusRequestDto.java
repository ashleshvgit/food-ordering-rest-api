package com.ashlesh.mycoolproject.dto.request;

import com.ashlesh.mycoolproject.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusRequestDto {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatusRequestDto() {
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}