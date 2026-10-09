package com.ashlesh.mycoolproject.mapper;

import com.ashlesh.mycoolproject.dto.response.CartItemResponseDto;
import com.ashlesh.mycoolproject.dto.response.CartResponseDto;
import com.ashlesh.mycoolproject.entity.Cart;
import com.ashlesh.mycoolproject.entity.CartItem;
import com.ashlesh.mycoolproject.entity.FoodItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartMapper {

    // Converts the cart to a response and calculates subtotals and total
    public CartResponseDto toResponseDto(Cart cart) {
        CartResponseDto responseDto = new CartResponseDto();
        responseDto.setCartId(cart.getId());
        responseDto.setCustomerId(cart.getCustomer().getId());

        if (cart.getRestaurant() != null) {
            responseDto.setRestaurantId(cart.getRestaurant().getId());
            responseDto.setRestaurantName(cart.getRestaurant().getName());
        }

        List<CartItemResponseDto> itemDtoList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            FoodItem foodItem = cartItem.getFoodItem();
            BigDecimal subtotal = foodItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            CartItemResponseDto itemDto = new CartItemResponseDto();
            itemDto.setFoodItemId(foodItem.getId());
            itemDto.setFoodItemName(foodItem.getName());
            itemDto.setUnitPrice(foodItem.getPrice());
            itemDto.setQuantity(cartItem.getQuantity());
            itemDto.setSubtotal(subtotal);
            itemDtoList.add(itemDto);

            totalAmount = totalAmount.add(subtotal);
        }

        responseDto.setItems(itemDtoList);
        responseDto.setTotalAmount(totalAmount);
        return responseDto;
    }
}