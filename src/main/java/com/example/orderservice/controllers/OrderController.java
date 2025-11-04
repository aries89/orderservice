package com.example.orderservice.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.dtos.OrderRequestDto;
import com.example.orderservice.dtos.OrderResponseDto;
import com.example.orderservice.dtos.OrderUpdateDto;
import com.example.orderservice.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	
	private final OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto){
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequestDto));
	}

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDto orderUpdateDto){
    	return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrder(id,orderUpdateDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id){
    	return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(Pageable pageable){
    	return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
    	orderService.deleteOrder(id);
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable Long userId){
    	return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByStatus(
            @RequestParam String status,
            Pageable pageable){
    	return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByStatus(status, pageable));
    }

}
