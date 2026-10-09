package com.ashlesh.mycoolproject.dto.request;

import jakarta.validation.constraints.NotNull;

public class FoodItemAvailabilityRequestDto {

    @NotNull(message = "available is required")
    private Boolean available;

    public FoodItemAvailabilityRequestDto() {
    }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}