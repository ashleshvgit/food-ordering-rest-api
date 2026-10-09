package com.ashlesh.mycoolproject.mapper;

import com.ashlesh.mycoolproject.dto.request.RestaurantRequestDto;
import com.ashlesh.mycoolproject.dto.response.RestaurantResponseDto;
import com.ashlesh.mycoolproject.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    // Request DTO -> Entity (used while creating)
    public Restaurant toEntity(RestaurantRequestDto requestDto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(requestDto.getName().trim());
        restaurant.setDescription(requestDto.getDescription());
        restaurant.setAddress(requestDto.getAddress().trim());
        restaurant.setPhone(requestDto.getPhone());

        if (requestDto.getActive() != null) {
            restaurant.setActive(requestDto.getActive());
        } else {
            restaurant.setActive(true);
        }
        return restaurant;
    }

    // Copies new values into an existing entity (used while updating)
    public void updateEntity(Restaurant restaurant, RestaurantRequestDto requestDto) {
        restaurant.setName(requestDto.getName().trim());
        restaurant.setDescription(requestDto.getDescription());
        restaurant.setAddress(requestDto.getAddress().trim());
        restaurant.setPhone(requestDto.getPhone());

        if (requestDto.getActive() != null) {
            restaurant.setActive(requestDto.getActive());
        }
    }

    // Entity -> Response DTO
    public RestaurantResponseDto toResponseDto(Restaurant restaurant) {
        RestaurantResponseDto responseDto = new RestaurantResponseDto();
        responseDto.setId(restaurant.getId());
        responseDto.setName(restaurant.getName());
        responseDto.setDescription(restaurant.getDescription());
        responseDto.setAddress(restaurant.getAddress());
        responseDto.setPhone(restaurant.getPhone());
        responseDto.setActive(restaurant.isActive());
        responseDto.setCreatedAt(restaurant.getCreatedAt());
        responseDto.setUpdatedAt(restaurant.getUpdatedAt());
        return responseDto;
    }
}