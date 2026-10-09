package com.ashlesh.mycoolproject.dto.request;



import jakarta.validation.constraints.NotNull;

public class RestaurantStatusRequestDto {

    @NotNull(message = "active is required")
    private Boolean active;

    public RestaurantStatusRequestDto() {
    }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}