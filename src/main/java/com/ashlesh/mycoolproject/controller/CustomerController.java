package com.ashlesh.mycoolproject.controller;

import com.ashlesh.mycoolproject.dto.request.CustomerRequestDto;
import com.ashlesh.mycoolproject.dto.response.CustomerResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Controller", description = "APIs to manage customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Register a customer", description = "Creates a customer. Email must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto requestDto) {
        return new ResponseEntity<>(customerService.create(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all customers",
            description = "Returns customers page by page. Supports search by name and sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers fetched"),
            @ApiResponse(responseCode = "400", description = "Invalid paging or sorting value")
    })
    @GetMapping
    public ResponseEntity<PageResponse<CustomerResponseDto>> getAllCustomers(
            @Parameter(description = "Search customers whose name contains this text (optional)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Customers per page (1 to 50)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, name, email or createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String direction) {
        return new ResponseEntity<>(customerService.getAll(name, page, size, sortBy, direction), HttpStatus.OK);
    }

    @Operation(summary = "Get a customer by id", description = "Returns details of one customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long id) {
        return new ResponseEntity<>(customerService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update a customer", description = "Updates customer details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Email used by another customer")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto requestDto) {
        return new ResponseEntity<>(customerService.update(id, requestDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a customer",
            description = "Deletes a customer. Not allowed if the customer has a cart or orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Customer is linked with a cart or orders")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Id of the customer", example = "1") @PathVariable Long id) {
        customerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}