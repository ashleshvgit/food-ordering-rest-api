package com.ashlesh.mycoolproject.controller;

import com.ashlesh.mycoolproject.dto.request.OrderRequestDto;
import com.ashlesh.mycoolproject.dto.request.OrderStatusRequestDto;
import com.ashlesh.mycoolproject.dto.response.OrderResponseDto;
import com.ashlesh.mycoolproject.dto.response.PageResponse;
import com.ashlesh.mycoolproject.enums.OrderStatus;
import com.ashlesh.mycoolproject.service.OrderService;
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
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "APIs to place and track orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Place an order",
            description = "Creates an order from the customer's cart and then clears the cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "422", description = "Cart empty, restaurant inactive or food unavailable")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        return new ResponseEntity<>(orderService.placeOrder(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all orders",
            description = "Returns orders page by page. Can filter by customer and status, and supports sorting.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders fetched"),
            @ApiResponse(responseCode = "400", description = "Invalid paging, sorting or filter value")
    })
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrders(
            @Parameter(description = "Filter by customer id (optional)")
            @RequestParam(required = false) Long customerId,
            @Parameter(description = "Filter by status: PLACED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED (optional)")
            @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Orders per page (1 to 50)")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: id, totalAmount, status or createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String direction) {

        return new ResponseEntity<>(
                orderService.getAll(customerId, status, page, size, sortBy, direction), HttpStatus.OK);
    }

    @Operation(summary = "Get an order by id", description = "Returns an order with its items and total.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "Id of the order", example = "1") @PathVariable Long id) {
        return new ResponseEntity<>(orderService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update order status",
            description = "Moves the order to the next status. Allowed flow: PLACED > CONFIRMED > PREPARING > "
                    + "OUT_FOR_DELIVERY > DELIVERED. PLACED or CONFIRMED can be CANCELLED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Status missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "422", description = "This status change is not allowed")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @Parameter(description = "Id of the order", example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequestDto statusDto) {
        return new ResponseEntity<>(orderService.updateStatus(id, statusDto), HttpStatus.OK);
    }
}