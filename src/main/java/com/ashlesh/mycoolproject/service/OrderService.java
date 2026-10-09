package com.ashlesh.mycoolproject.service;

import com.ashlesh.mycoolproject.dto.request.OrderRequestDto;
import com.ashlesh.mycoolproject.dto.request.OrderStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.OrderResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.enums.OrderStatus;

public interface OrderService {

    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    OrderResponseDto getById(Long id);

    PageResponse<OrderResponseDto> getAll(Long customerId, OrderStatus status,
                                          int page, int size, String sortBy, String direction);

    OrderResponseDto updateStatus(Long id, OrderStatusRequestDto statusDto);
}