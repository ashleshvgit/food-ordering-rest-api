package com.ashlesh.mycoolproject.service.impl;

import com.ashlesh.mycoolproject.dto.request.RestaurantRequestDto;
import com.ashlesh.mycoolproject.dto.request.RestaurantStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.dto.response.RestaurantResponseDto;
import com.ashlesh.mycoolproject.entity.Restaurant;
import com.ashlesh.mycoolproject.exception.DuplicateResourceException;
import com.ashlesh.mycoolproject.exception.ResourceNotFoundException;
import com.ashlesh.mycoolproject.mapper.RestaurantMapper;
import com.ashlesh.mycoolproject.repository.RestaurantRepository;
import com.ashlesh.mycoolproject.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final List<String> ALLOWED_SORT_FIELDS = List.of("id", "name", "createdAt", "updatedAt");

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    // Constructor injection
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    @Transactional
    public RestaurantResponseDto create(RestaurantRequestDto requestDto) {
        String name = requestDto.getName().trim();
        String address = requestDto.getAddress().trim();

        // Business rule: same name at the same address is not allowed
        if (restaurantRepository.existsByNameIgnoreCaseAndAddressIgnoreCase(name, address)) {
            throw new DuplicateResourceException("Restaurant '" + name + "' already exists at this address");
        }

        Restaurant restaurant = restaurantMapper.toEntity(requestDto);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDto(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDto getById(Long id) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        return restaurantMapper.toResponseDto(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RestaurantResponseDto> getAll(String name, Boolean active,
                                                      int page, int size, String sortBy, String direction) {
        // 1. Validate page, size, sortBy and direction
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy '" + sortBy + "'. Allowed fields: " + ALLOWED_SORT_FIELDS);
        }

        Sort sort;
        if (direction.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            throw new IllegalArgumentException("Direction must be asc or desc");
        }

        // 2. Build the Pageable (page number + size + sorting)
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Call the repository method that matches the filters given
        boolean hasName = name != null && !name.trim().isEmpty();
        Page<Restaurant> restaurantPage;

        if (hasName && active != null) {
            restaurantPage = restaurantRepository.findByNameContainingIgnoreCaseAndActive(name.trim(), active, pageable);
        } else if (hasName) {
            restaurantPage = restaurantRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (active != null) {
            restaurantPage = restaurantRepository.findByActive(active, pageable);
        } else {
            restaurantPage = restaurantRepository.findAll(pageable);
        }

        // 4. Convert every entity to a response DTO
        List<RestaurantResponseDto> responseList = new ArrayList<>();
        for (Restaurant restaurant : restaurantPage.getContent()) {
            responseList.add(restaurantMapper.toResponseDto(restaurant));
        }

        // 5. Build our own page response
        return new PageResponse<>(responseList,
                restaurantPage.getNumber(),
                restaurantPage.getSize(),
                restaurantPage.getTotalElements(),
                restaurantPage.getTotalPages(),
                restaurantPage.hasNext());
    }

    @Override
    @Transactional
    public RestaurantResponseDto update(Long id, RestaurantRequestDto requestDto) {
        Restaurant restaurant = findRestaurantOrThrow(id);

        String name = requestDto.getName().trim();
        String address = requestDto.getAddress().trim();

        // Duplicate check, ignoring this same restaurant
        if (restaurantRepository.existsByNameIgnoreCaseAndAddressIgnoreCaseAndIdNot(name, address, id)) {
            throw new DuplicateResourceException("Restaurant '" + name + "' already exists at this address");
        }

        restaurantMapper.updateEntity(restaurant, requestDto);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDto(updatedRestaurant);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional
    public RestaurantResponseDto updateStatus(Long id, RestaurantStatusRequestDto statusDto) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurant.setActive(statusDto.getActive());
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDto(updatedRestaurant);
    }

    // Used by many methods: find by id or throw 404
    private Restaurant findRestaurantOrThrow(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }
}
