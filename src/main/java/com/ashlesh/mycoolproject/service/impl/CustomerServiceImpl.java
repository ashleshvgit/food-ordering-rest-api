package com.ashlesh.mycoolproject.service.impl;

import com.ashlesh.mycoolproject.dto.request.CustomerRequestDto;
import com.ashlesh.mycoolproject.dto.response.CustomerResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.entity.Customer;
import com.ashlesh.mycoolproject.exception.DuplicateResourceException;
import com.ashlesh.mycoolproject.exception.ResourceNotFoundException;
import com.ashlesh.mycoolproject.mapper.CustomerMapper;
import com.ashlesh.mycoolproject.repository.CustomerRepository;
import com.ashlesh.mycoolproject.service.CustomerService;
import com.ashlesh.mycoolproject.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("id", "name", "email", "createdAt");

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerResponseDto create(CustomerRequestDto requestDto) {
        String email = requestDto.getEmail().trim();
        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Customer with email '" + email + "' already exists");
        }
        Customer savedCustomer = customerRepository.save(customerMapper.toEntity(requestDto));
        return customerMapper.toResponseDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getById(Long id) {
        return customerMapper.toResponseDto(findCustomerOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponseDto> getAll(String name, int page, int size,
                                                    String sortBy, String direction) {
        Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, direction, ALLOWED_SORT_FIELDS);

        Page<Customer> customerPage;
        if (name != null && !name.trim().isEmpty()) {
            customerPage = customerRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        List<CustomerResponseDto> responseList = new ArrayList<>();
        for (Customer customer : customerPage.getContent()) {
            responseList.add(customerMapper.toResponseDto(customer));
        }

        return new PageResponse<>(responseList,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.hasNext());
    }

    @Override
    @Transactional
    public CustomerResponseDto update(Long id, CustomerRequestDto requestDto) {
        Customer customer = findCustomerOrThrow(id);

        String email = requestDto.getEmail().trim();
        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Another customer with email '" + email + "' already exists");
        }

        customerMapper.updateEntity(customer, requestDto);
        return customerMapper.toResponseDto(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customerRepository.delete(findCustomerOrThrow(id));
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}