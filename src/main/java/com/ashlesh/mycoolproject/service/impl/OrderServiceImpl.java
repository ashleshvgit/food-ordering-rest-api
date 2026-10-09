package com.ashlesh.mycoolproject.service.impl;

import com.ashlesh.mycoolproject.dto.request.OrderRequestDto;
import com.ashlesh.mycoolproject.dto.request.OrderStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.OrderResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.entity.*;
import com.ashlesh.mycoolproject.enums.OrderStatus;
import com.ashlesh.mycoolproject.exception.BusinessRuleException;
import com.ashlesh.mycoolproject.exception.ResourceNotFoundException;
import com.ashlesh.mycoolproject.mapper.OrderMapper;
import com.ashlesh.mycoolproject.repository.CartRepository;
import com.ashlesh.mycoolproject.repository.CustomerRepository;
import com.ashlesh.mycoolproject.repository.OrderRepository;
import com.ashlesh.mycoolproject.service.OrderService;
import com.ashlesh.mycoolproject.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("id", "totalAmount", "status", "createdAt");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            CustomerRepository customerRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }

    // Creating the order AND clearing the cart happen in ONE transaction.
    // If anything fails, both are rolled back.
    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", requestDto.getCustomerId()));

        // Rule 1: cart must exist and must not be empty
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessRuleException("Cart is empty. Add items before placing an order"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException("Cart is empty. Add items before placing an order");
        }

        // Rule 2: restaurant must still be active
        Restaurant restaurant = cart.getRestaurant();
        if (!restaurant.isActive()) {
            throw new BusinessRuleException("Restaurant '" + restaurant.getName() + "' is not active");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PLACED);

        // Use the address from the request, or the customer's saved address
        String address = requestDto.getDeliveryAddress();
        if (address == null || address.trim().isEmpty()) {
            order.setDeliveryAddress(customer.getAddress());
        } else {
            order.setDeliveryAddress(address.trim());
        }

        // Convert every cart item to an order item and calculate the total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            FoodItem foodItem = cartItem.getFoodItem();

            // Rule 3: every food item must still be available
            if (!foodItem.isAvailable()) {
                throw new BusinessRuleException(
                        "'" + foodItem.getName() + "' is no longer available. Remove it from the cart");
            }

            BigDecimal subtotal = foodItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItemId(foodItem.getId());
            orderItem.setFoodItemName(foodItem.getName());   // snapshot of name
            orderItem.setUnitPrice(foodItem.getPrice());     // snapshot of price
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);
            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after the order is saved
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);

        return orderMapper.toResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getById(Long id) {
        return orderMapper.toResponseDto(findOrderOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponseDto> getAll(Long customerId, OrderStatus status,
                                                 int page, int size, String sortBy, String direction) {
        Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, direction, ALLOWED_SORT_FIELDS);

        Page<Order> orderPage;
        if (customerId != null && status != null) {
            orderPage = orderRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null) {
            orderPage = orderRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderResponseDto> responseList = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            responseList.add(orderMapper.toResponseDto(order));
        }

        return new PageResponse<>(responseList,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.hasNext());
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long id, OrderStatusRequestDto statusDto) {
        Order order = findOrderOrThrow(id);
        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = statusDto.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BusinessRuleException(
                    "Cannot change order status from " + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    // Allowed status flow:
    // PLACED -> CONFIRMED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
    // PLACED or CONFIRMED can also go to CANCELLED. DELIVERED and CANCELLED are final.
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PLACED:
                return next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED:
                return next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING:
                return next == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return next == OrderStatus.DELIVERED;
            default:
                return false;
        }
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}