package com.example.orderservice.dtos;

import java.util.List;

import com.example.orderservice.enums.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {

	@NotNull
	private Long userId;

	@NotNull
	@NotEmpty
	private List<OrderProductDto> products;

	private OrderStatus status; // optional, default CREATED

	public OrderRequestDto() {
	
	}

	public OrderRequestDto(Long userId,List<OrderProductDto> products, OrderStatus status) {
		this.userId = userId;
		this.products = products;
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<OrderProductDto> getProducts() {
		return products;
	}

	public void setProducts(List<OrderProductDto> products) {
		this.products = products;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	

}
