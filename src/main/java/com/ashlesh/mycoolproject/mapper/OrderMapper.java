package com.ashlesh.mycoolproject.mapper;

import com.ashlesh.mycoolproject.dto.response.OrderItemResponseDto;
import com.ashlesh.mycoolproject.dto.response.OrderResponseDto;
import com.ashlesh.mycoolproject.entity.Order;
import com.ashlesh.mycoolproject.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDto toResponseDto(Order order) {
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setId(order.getId());
        responseDto.setCustomerId(order.getCustomer().getId());
        responseDto.setCustomerName(order.getCustomer().getName());
        responseDto.setRestaurantId(order.getRestaurant().getId());
        responseDto.setRestaurantName(order.getRestaurant().getName());
        responseDto.setStatus(order.getStatus());
        responseDto.setTotalAmount(order.getTotalAmount());
        responseDto.setDeliveryAddress(order.getDeliveryAddress());
        responseDto.setCreatedAt(order.getCreatedAt());
        responseDto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponseDto> itemDtoList = new ArrayList<>();
        for (OrderItem orderItem : order.getItems()) {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setFoodItemId(orderItem.getFoodItemId());
            itemDto.setFoodItemName(orderItem.getFoodItemName());
            itemDto.setUnitPrice(orderItem.getUnitPrice());
            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setSubtotal(orderItem.getSubtotal());
            itemDtoList.add(itemDto);
        }
        responseDto.setItems(itemDtoList);
        return responseDto;
    }
}