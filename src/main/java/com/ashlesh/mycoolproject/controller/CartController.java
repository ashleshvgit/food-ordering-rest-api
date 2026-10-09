package com.ashlesh.mycoolproject.controller;

import com.ashlesh.mycoolproject.dto.request.CartItemQuantityRequestDto;
import com.ashlesh.mycoolproject.dto.request.CartItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.CartResponseDto;
import com.ashlesh.mycoolproject.service.CartService;
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
@RequestMapping("/api/v1/customers/{customerId}/cart")
@Tag(name = "Cart Controller", description = "APIs to manage the shopping cart of a customer")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "View cart", description = "Shows the cart items with subtotals and total amount.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart fetched"),
            @ApiResponse(responseCode = "404", description = "Customer or cart not found")
    })
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long customerId) {
        return new ResponseEntity<>(cartService.getCart(customerId), HttpStatus.OK);
    }

    @Operation(summary = "Add item to cart",
            description = "Adds a food item to the cart. If it is already in the cart, the quantity is increased. "
                    + "A cart can contain food from only one restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added, updated cart returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Customer or food item not found"),
            @ApiResponse(responseCode = "422", description = "Food unavailable, restaurant inactive or cart has another restaurant's items")
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long customerId,
            @Valid @RequestBody CartItemRequestDto requestDto) {
        return new ResponseEntity<>(cartService.addItem(customerId, requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Change quantity of a cart item", description = "Sets a new quantity for an item already in the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Customer, cart or cart item not found")
    })
    @PutMapping("/items/{foodItemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long customerId,
            @Parameter(description = "Id of the food item in the cart", example = "1") @PathVariable Long foodItemId,
            @Valid @RequestBody CartItemQuantityRequestDto quantityDto) {
        return new ResponseEntity<>(cartService.updateItemQuantity(customerId, foodItemId, quantityDto), HttpStatus.OK);
    }

    @Operation(summary = "Remove an item from cart", description = "Removes one food item from the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed, updated cart returned"),
            @ApiResponse(responseCode = "404", description = "Customer, cart or cart item not found")
    })
    @DeleteMapping("/items/{foodItemId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long customerId,
            @Parameter(description = "Id of the food item to remove", example = "1") @PathVariable Long foodItemId) {
        return new ResponseEntity<>(cartService.removeItem(customerId, foodItemId), HttpStatus.OK);
    }

    @Operation(summary = "Clear cart", description = "Removes all items from the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared"),
            @ApiResponse(responseCode = "404", description = "Customer or cart not found")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}