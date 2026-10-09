package com.ashlesh.mycoolproject.service;

import com.ashlesh.mycoolproject.dto.request.CustomerRequestDto;
import com.ashlesh.mycoolproject.dto.response.CustomerResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;

public interface CustomerService {

    CustomerResponseDto create(CustomerRequestDto requestDto);

    CustomerResponseDto getById(Long id);

    PageResponse<CustomerResponseDto> getAll(String name, int page, int size, String sortBy, String direction);

    CustomerResponseDto update(Long id, CustomerRequestDto requestDto);

    void delete(Long id);
}