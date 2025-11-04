package com.example.orderservice.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.orderservice.dtos.OrderRequestDto;
import com.example.orderservice.dtos.OrderResponseDto;
import com.example.orderservice.dtos.OrderUpdateDto;

public interface OrderService {
	
	OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto updateOrder(Long id, OrderUpdateDto orderUpdateDto);

    OrderResponseDto getOrderById(Long id);

    Page<OrderResponseDto> getAllOrders(Pageable pageable);

    void deleteOrder(Long id);

    List<OrderResponseDto> getOrdersByUserId(Long userId);

    Page<OrderResponseDto> getOrdersByStatus(String status, Pageable pageable);

}
