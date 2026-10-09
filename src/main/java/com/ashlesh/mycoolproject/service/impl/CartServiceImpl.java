package com.ashlesh.mycoolproject.service.impl;

import com.ashlesh.mycoolproject.dto.request.CartItemQuantityRequestDto;
import com.ashlesh.mycoolproject.dto.request.CartItemRequestDto;
import com.ashlesh.mycoolproject.dto.response.CartResponseDto;
import com.ashlesh.mycoolproject.entity.Cart;
import com.ashlesh.mycoolproject.entity.CartItem;
import com.ashlesh.mycoolproject.entity.Customer;
import com.ashlesh.mycoolproject.entity.FoodItem;
import com.ashlesh.mycoolproject.exception.BusinessRuleException;
import com.ashlesh.mycoolproject.exception.ResourceNotFoundException;
import com.ashlesh.mycoolproject.mapper.CartMapper;
import com.ashlesh.mycoolproject.repository.CartRepository;
import com.ashlesh.mycoolproject.repository.CustomerRepository;
import com.ashlesh.mycoolproject.repository.FoodItemRepository;
import com.ashlesh.mycoolproject.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private static final int MAX_QUANTITY_PER_ITEM = 20;

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final FoodItemRepository foodItemRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CustomerRepository customerRepository,
                           FoodItemRepository foodItemRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.foodItemRepository = foodItemRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long customerId) {
        return cartMapper.toResponseDto(findCartOrThrow(customerId));
    }

    @Override
    @Transactional
    public CartResponseDto addItem(Long customerId, CartItemRequestDto requestDto) {
        Customer customer = findCustomerOrThrow(customerId);

        FoodItem foodItem = foodItemRepository.findById(requestDto.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item", requestDto.getFoodItemId()));

        // Rule 1: food must be available and its restaurant must be active
        if (!foodItem.isAvailable()) {
            throw new BusinessRuleException("Food item '" + foodItem.getName() + "' is not available");
        }
        if (!foodItem.getRestaurant().isActive()) {
            throw new BusinessRuleException("Restaurant '" + foodItem.getRestaurant().getName() + "' is not active");
        }

        // Get the customer's cart, or create a new one
        Cart cart = cartRepository.findByCustomerId(customerId).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
        }

        // Rule 2: one cart can contain food from only one restaurant
        if (!cart.getItems().isEmpty()
                && !cart.getRestaurant().getId().equals(foodItem.getRestaurant().getId())) {
            throw new BusinessRuleException(
                    "Your cart has items from another restaurant. Clear the cart before adding this item");
        }
        cart.setRestaurant(foodItem.getRestaurant());

        // If the food is already in the cart, increase the quantity. Otherwise add a new line.
        CartItem existingItem = findCartItem(cart, foodItem.getId());
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + requestDto.getQuantity();
            if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                throw new BusinessRuleException("Quantity of one item cannot be more than " + MAX_QUANTITY_PER_ITEM);
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setFoodItem(foodItem);
            newItem.setQuantity(requestDto.getQuantity());
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDto(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDto updateItemQuantity(Long customerId, Long foodItemId,
                                              CartItemQuantityRequestDto quantityDto) {
        Cart cart = findCartOrThrow(customerId);

        CartItem cartItem = findCartItem(cart, foodItemId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Cart item", foodItemId);
        }

        cartItem.setQuantity(quantityDto.getQuantity());
        return cartMapper.toResponseDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponseDto removeItem(Long customerId, Long foodItemId) {
        Cart cart = findCartOrThrow(customerId);

        boolean removed = cart.getItems().removeIf(item -> item.getFoodItem().getId().equals(foodItemId));
        if (!removed) {
            throw new ResourceNotFoundException("Cart item", foodItemId);
        }

        // Empty cart is not linked to any restaurant
        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null);
        }
        return cartMapper.toResponseDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = findCartOrThrow(customerId);
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);
    }

    // ----- small private helper methods -----

    private Customer findCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
    }

    private Cart findCartOrThrow(Long customerId) {
        findCustomerOrThrow(customerId); // gives a clear 404 if the customer itself does not exist
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart for customer", customerId));
    }

    private CartItem findCartItem(Cart cart, Long foodItemId) {
        for (CartItem item : cart.getItems()) {
            if (item.getFoodItem().getId().equals(foodItemId)) {
                return item;
            }
        }
        return null;
    }
}