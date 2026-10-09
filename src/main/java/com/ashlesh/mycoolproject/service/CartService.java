package com.ashlesh.mycoolproject.service;

import com.ashlesh.mycoolproject.dto.request.CartItemQuantityRequestDto;
import com.ashlesh.mycoolproject.dto.request.CartItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto getCart(Long customerId);

    CartResponseDto addItem(Long customerId, CartItemRequestDto requestDto);

    CartResponseDto updateItemQuantity(Long customerId, Long foodItemId, CartItemQuantityRequestDto quantityDto);

    CartResponseDto removeItem(Long customerId, Long foodItemId);

    void clearCart(Long customerId);
}