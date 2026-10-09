package com.ashlesh.mycoolproject.controller;

import com.ashlesh.mycoolproject.dto.request.RestaurantRequestDto;
import com.ashlesh.mycoolproject.dto.request.RestaurantStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.dto.response.RestaurantResponseDto;
import com.ashlesh.mycoolproject.service.RestaurantService;
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
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant Controller", description = "APIs to add, view, update, delete and activate restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Constructor injection
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "Add a new restaurant",
            description = "Saves a new restaurant. The same name at the same address is not allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (blank name, wrong phone, etc.)"),
            @ApiResponse(responseCode = "409", description = "Restaurant with same name and address already exists")
    })
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto responseDto = restaurantService.create(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all restaurants",
            description = "Returns restaurants page by page. Supports sorting and filtering by name and active status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurants fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size, sortBy or direction")
    })
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantResponseDto>> getAllRestaurants(
            @Parameter(description = "Search restaurants whose name contains this text (optional)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by active status: true or false (optional)")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of restaurants per page (1 to 50)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by: id, name, createdAt or updatedAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String direction) {

        PageResponse<RestaurantResponseDto> response =
                restaurantService.getAll(name, active, page, size, sortBy, direction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get a restaurant by id",
            description = "Returns the details of one restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(
            @Parameter(description = "Id of the restaurant", example = "1")
            @PathVariable Long id) {
        return new ResponseEntity<>(restaurantService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update a restaurant",
            description = "Replaces the details of an existing restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "409", description = "Another restaurant with same name and address exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @Parameter(description = "Id of the restaurant to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDto requestDto) {
        return new ResponseEntity<>(restaurantService.update(id, requestDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a restaurant",
            description = "Permanently deletes a restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "Id of the restaurant to delete", example = "1")
            @PathVariable Long id) {
        restaurantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Activate or deactivate a restaurant",
            description = "Changes only the active status of the restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "active value is missing"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantResponseDto> updateRestaurantStatus(
            @Parameter(description = "Id of the restaurant", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestaurantStatusRequestDto statusDto) {
        return new ResponseEntity<>(restaurantService.updateStatus(id, statusDto), HttpStatus.OK);
    }
}