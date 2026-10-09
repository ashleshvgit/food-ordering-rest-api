package com.ashlesh.mycoolproject.dto.request;

import com.ashlesh.mycoolproject.enums.FoodCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class FoodItemRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price can have up to 8 digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private FoodCategory category;

    // Optional. Default is true for a new food item.
    private Boolean available;

    @NotNull(message = "Restaurant id is required")
    private Long restaurantId;

    public FoodItemRequestDto() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
}