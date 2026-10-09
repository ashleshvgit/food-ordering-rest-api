package com.ashlesh.mycoolproject.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartResponseDto {

    private Long cartId;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private List<CartItemResponseDto> items = new ArrayList<>();
    private BigDecimal totalAmount;

    public CartResponseDto() {
    }

    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public List<CartItemResponseDto> getItems() { return items; }
    public void setItems(List<CartItemResponseDto> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}