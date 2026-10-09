package com.ashlesh.mycoolproject.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrderRequestDto {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    // Optional. If not sent, the customer's saved address is used.
    @Size(max = 255, message = "Delivery address must be at most 255 characters")
    private String deliveryAddress;

    public OrderRequestDto() {
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}