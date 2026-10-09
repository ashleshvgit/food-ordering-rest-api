package com.ashlesh.mycoolproject.controller;

import com.ashlesh.mycoolproject.dto.request.FoodItemAvailabilityRequestDto;
import com.ashlesh.mycoolproject.dto.request.FoodItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.FoodItemResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.enums.FoodCategory;
import com.ashlesh.mycoolproject.service.FoodItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/food-items")
@Tag(name = "Food Item Controller", description = "APIs to manage food items of restaurants")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @Operation(summary = "Add a food item",
            description = "Adds a food item to an active restaurant. Same name in the same restaurant is not allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Food item created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "409", description = "Food item already exists in this restaurant"),
            @ApiResponse(responseCode = "422", description = "Restaurant is inactive")
    })
    @PostMapping
    public ResponseEntity<FoodItemResponseDto> createFoodItem(@Valid @RequestBody FoodItemRequestDto requestDto) {
        return new ResponseEntity<>(foodItemService.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all food items",
            description = "Returns food items page by page. Can filter by restaurant and category, and supports sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food items fetched"),
            @ApiResponse(responseCode = "400", description = "Invalid paging, sorting or filter value")
    })
    @GetMapping
    public ResponseEntity<PageResponse<FoodItemResponseDto>> getAllFoodItems(
            @Parameter(description = "Filter by restaurant id (optional)")
            @RequestParam(required = false) Long restaurantId,
            @Parameter(description = "Filter by category: STARTER, MAIN_COURSE, DESSERT, BEVERAGE, SNACK (optional)")
            @RequestParam(required = false) FoodCategory category,
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page (1 to 50)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, name, price, category or createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String direction) {

        return new ResponseEntity<>(
                foodItemService.getAll(restaurantId, category, page, size, sortBy, direction), HttpStatus.OK);
    }

    @Operation(summary = "Get a food item by id", description = "Returns details of one food item.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item found"),
            @ApiResponse(responseCode = "404", description = "Food item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponseDto> getFoodItemById(
            @Parameter(description = "Id of the food item", example = "1") @PathVariable Long id) {
        return new ResponseEntity<>(foodItemService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update a food item",
            description = "Updates a food item. The restaurant of a food item cannot be changed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Food item updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Food item not found"),
            @ApiResponse(responseCode = "409", description = "Another food item with this name exists in the restaurant"),
            @ApiResponse(responseCode = "422", description = "Tried to move food item to another restaurant")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponseDto> updateFoodItem(
            @Parameter(description = "Id of the food item", example = "1") @PathVariable Long id,
            @Valid @RequestBody FoodItemRequestDto requestDto) {
        return new ResponseEntity<>(foodItemService.update(id, requestDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a food item",
            description = "Deletes a food item. Not allowed if it is currently in a cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Food item deleted"),
            @ApiResponse(responseCode = "404", description = "Food item not found"),
            @ApiResponse(responseCode = "409", description = "Food item is linked with other data")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(
            @Parameter(description = "Id of the food item", example = "1") @PathVariable Long id) {
        foodItemService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Change availability of a food item",
            description = "Marks a food item as available or unavailable (for example sold out).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Availability updated"),
            @ApiResponse(responseCode = "400", description = "available value is missing"),
            @ApiResponse(responseCode = "404", description = "Food item not found")
    })
    @PatchMapping("/{id}/availability")
    public ResponseEntity<FoodItemResponseDto> updateAvailability(
            @Parameter(description = "Id of the food item", example = "1") @PathVariable Long id,
            @Valid @RequestBody FoodItemAvailabilityRequestDto availabilityDto) {
        return new ResponseEntity<>(foodItemService.updateAvailability(id, availabilityDto), HttpStatus.OK);
    }
}