package com.ashlesh.mycoolproject.mapper;

import com.ashlesh.mycoolproject.dto.request.FoodItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.FoodItemResponseDto;
import com.ashlesh.mycoolproject.entity.FoodItem;
import com.ashlesh.mycoolproject.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class FoodItemMapper {

    public FoodItem toEntity(FoodItemRequestDto requestDto, Restaurant restaurant) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(requestDto.getName().trim());
        foodItem.setDescription(requestDto.getDescription());
        foodItem.setPrice(requestDto.getPrice());
        foodItem.setCategory(requestDto.getCategory());
        foodItem.setRestaurant(restaurant);

        if (requestDto.getAvailable() != null) {
            foodItem.setAvailable(requestDto.getAvailable());
        } else {
            foodItem.setAvailable(true);
        }
        return foodItem;
    }

    public void updateEntity(FoodItem foodItem, FoodItemRequestDto requestDto) {
        foodItem.setName(requestDto.getName().trim());
        foodItem.setDescription(requestDto.getDescription());
        foodItem.setPrice(requestDto.getPrice());
        foodItem.setCategory(requestDto.getCategory());

        if (requestDto.getAvailable() != null) {
            foodItem.setAvailable(requestDto.getAvailable());
        }
    }

    public FoodItemResponseDto toResponseDto(FoodItem foodItem) {
        FoodItemResponseDto responseDto = new FoodItemResponseDto();
        responseDto.setId(foodItem.getId());
        responseDto.setName(foodItem.getName());
        responseDto.setDescription(foodItem.getDescription());
        responseDto.setPrice(foodItem.getPrice());
        responseDto.setCategory(foodItem.getCategory());
        responseDto.setAvailable(foodItem.isAvailable());
        responseDto.setRestaurantId(foodItem.getRestaurant().getId());
        responseDto.setRestaurantName(foodItem.getRestaurant().getName());
        responseDto.setCreatedAt(foodItem.getCreatedAt());
        responseDto.setUpdatedAt(foodItem.getUpdatedAt());
        return responseDto;
    }
}