package com.ashlesh.mycoolproject.service.impl;

import com.ashlesh.mycoolproject.dto.request.FoodItemAvailabilityRequestDto;
import com.ashlesh.mycoolproject.dto.request.FoodItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.FoodItemResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.entity.FoodItem;
import com.ashlesh.mycoolproject.entity.Restaurant;
import com.ashlesh.mycoolproject.enums.FoodCategory;
import com.ashlesh.mycoolproject.exception.BusinessRuleException;
import com.ashlesh.mycoolproject.exception.DuplicateResourceException;
import com.ashlesh.mycoolproject.exception.ResourceNotFoundException;
import com.ashlesh.mycoolproject.mapper.FoodItemMapper;
import com.ashlesh.mycoolproject.repository.FoodItemRepository;
import com.ashlesh.mycoolproject.repository.RestaurantRepository;
import com.ashlesh.mycoolproject.service.FoodItemService;
import com.ashlesh.mycoolproject.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("id", "name", "price", "category", "createdAt");

    private final FoodItemRepository foodItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemMapper foodItemMapper;

    public FoodItemServiceImpl(FoodItemRepository foodItemRepository,
                               RestaurantRepository restaurantRepository,
                               FoodItemMapper foodItemMapper) {
        this.foodItemRepository = foodItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemMapper = foodItemMapper;
    }

    @Override
    @Transactional
    public FoodItemResponseDto create(FoodItemRequestDto requestDto) {
        // Rule 1: restaurant must exist
        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", requestDto.getRestaurantId()));

        // Rule 2: cannot add food to an inactive restaurant
        if (!restaurant.isActive()) {
            throw new BusinessRuleException("Cannot add food items to an inactive restaurant");
        }

        // Rule 3: same food name is not allowed twice in the same restaurant
        String name = requestDto.getName().trim();
        if (foodItemRepository.existsByNameIgnoreCaseAndRestaurantId(name, restaurant.getId())) {
            throw new DuplicateResourceException("Food item '" + name + "' already exists in this restaurant");
        }

        FoodItem foodItem = foodItemMapper.toEntity(requestDto, restaurant);
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toResponseDto(savedFoodItem);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodItemResponseDto getById(Long id) {
        return foodItemMapper.toResponseDto(findFoodItemOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FoodItemResponseDto> getAll(Long restaurantId, FoodCategory category,
                                                    int page, int size, String sortBy, String direction) {
        Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, direction, ALLOWED_SORT_FIELDS);

        Page<FoodItem> foodItemPage;
        if (restaurantId != null && category != null) {
            foodItemPage = foodItemRepository.findByRestaurantIdAndCategory(restaurantId, category, pageable);
        } else if (restaurantId != null) {
            foodItemPage = foodItemRepository.findByRestaurantId(restaurantId, pageable);
        } else if (category != null) {
            foodItemPage = foodItemRepository.findByCategory(category, pageable);
        } else {
            foodItemPage = foodItemRepository.findAll(pageable);
        }

        List<FoodItemResponseDto> responseList = new ArrayList<>();
        for (FoodItem foodItem : foodItemPage.getContent()) {
            responseList.add(foodItemMapper.toResponseDto(foodItem));
        }

        return new PageResponse<>(responseList,
                foodItemPage.getNumber(),
                foodItemPage.getSize(),
                foodItemPage.getTotalElements(),
                foodItemPage.getTotalPages(),
                foodItemPage.hasNext());
    }

    @Override
    @Transactional
    public FoodItemResponseDto update(Long id, FoodItemRequestDto requestDto) {
        FoodItem foodItem = findFoodItemOrThrow(id);

        // A food item cannot be moved to another restaurant
        if (!foodItem.getRestaurant().getId().equals(requestDto.getRestaurantId())) {
            throw new BusinessRuleException("A food item cannot be moved to another restaurant");
        }

        String name = requestDto.getName().trim();
        if (foodItemRepository.existsByNameIgnoreCaseAndRestaurantIdAndIdNot(
                name, requestDto.getRestaurantId(), id)) {
            throw new DuplicateResourceException("Food item '" + name + "' already exists in this restaurant");
        }

        foodItemMapper.updateEntity(foodItem, requestDto);
        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toResponseDto(updatedFoodItem);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FoodItem foodItem = findFoodItemOrThrow(id);
        foodItemRepository.delete(foodItem);
    }

    @Override
    @Transactional
    public FoodItemResponseDto updateAvailability(Long id, FoodItemAvailabilityRequestDto availabilityDto) {
        FoodItem foodItem = findFoodItemOrThrow(id);
        foodItem.setAvailable(availabilityDto.getAvailable());
        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toResponseDto(updatedFoodItem);
    }

    private FoodItem findFoodItemOrThrow(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item", id));
    }
}