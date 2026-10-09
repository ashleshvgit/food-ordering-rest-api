package com.ashlesh.mycoolproject.service;


import com.ashlesh.mycoolproject.dto.request.RestaurantRequestDto;
import com.ashlesh.mycoolproject.dto.request.RestaurantStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.dto.response.RestaurantResponseDto;

public interface RestaurantService {

    RestaurantResponseDto create(RestaurantRequestDto requestDto);

    RestaurantResponseDto getById(Long id);

    PageResponse<RestaurantResponseDto> getAll(String name, Boolean active,
                                               int page, int size, String sortBy, String direction);

    RestaurantResponseDto update(Long id, RestaurantRequestDto requestDto);

    void delete(Long id);

    RestaurantResponseDto updateStatus(Long id, RestaurantStatusRequestDto statusDto);
}