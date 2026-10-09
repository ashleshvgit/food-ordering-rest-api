package com.ashlesh.mycoolproject.mapper;

import com.ashlesh.mycoolproject.dto.request.CustomerRequestDto;
import com.ashlesh.mycoolproject.dto.response.CustomerResponseDto;
import com.ashlesh.mycoolproject.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequestDto requestDto) {
        Customer customer = new Customer();
        updateEntity(customer, requestDto);
        return customer;
    }

    public void updateEntity(Customer customer, CustomerRequestDto requestDto) {
        customer.setName(requestDto.getName().trim());
        // Email is stored in lower case so duplicates are easy to detect
        customer.setEmail(requestDto.getEmail().trim().toLowerCase());
        customer.setPhone(requestDto.getPhone());
        customer.setAddress(requestDto.getAddress().trim());
    }

    public CustomerResponseDto toResponseDto(Customer customer) {
        CustomerResponseDto responseDto = new CustomerResponseDto();
        responseDto.setId(customer.getId());
        responseDto.setName(customer.getName());
        responseDto.setEmail(customer.getEmail());
        responseDto.setPhone(customer.getPhone());
        responseDto.setAddress(customer.getAddress());
        responseDto.setCreatedAt(customer.getCreatedAt());
        responseDto.setUpdatedAt(customer.getUpdatedAt());
        return responseDto;
    }
}