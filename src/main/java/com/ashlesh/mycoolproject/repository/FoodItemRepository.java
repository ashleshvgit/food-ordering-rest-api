package com.ashlesh.mycoolproject.repository;

import com.ashlesh.mycoolproject.entity.FoodItem;
import com.ashlesh.mycoolproject.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    // Duplicate rule: same food name in the same restaurant
    boolean existsByNameIgnoreCaseAndRestaurantId(String name, Long restaurantId);

    boolean existsByNameIgnoreCaseAndRestaurantIdAndIdNot(String name, Long restaurantId, Long id);

    // Filters (RestaurantId means restaurant.id)
    Page<FoodItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<FoodItem> findByCategory(FoodCategory category, Pageable pageable);

    Page<FoodItem> findByRestaurantIdAndCategory(Long restaurantId, FoodCategory category, Pageable pageable);
}