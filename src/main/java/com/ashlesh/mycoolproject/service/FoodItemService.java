package com.ashlesh.mycoolproject.service;

import com.ashlesh.mycoolproject.dto.request.FoodItemAvailabilityRequestDto;
import com.ashlesh.mycoolproject.dto.request.FoodItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.FoodItemResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.enums.FoodCategory;

public interface FoodItemService {

    FoodItemResponseDto create(FoodItemRequestDto requestDto);

    FoodItemResponseDto getById(Long id);

    PageResponse<FoodItemResponseDto> getAll(Long restaurantId, FoodCategory category,
                                             int page, int size, String sortBy, String direction);

    FoodItemResponseDto update(Long id, FoodItemRequestDto requestDto);

    void delete(Long id);

    FoodItemResponseDto updateAvailability(Long id, FoodItemAvailabilityRequestDto availabilityDto);
}