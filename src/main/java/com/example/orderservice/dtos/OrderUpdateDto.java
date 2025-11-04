package com.example.orderservice.dtos;

import java.util.List;

import com.example.orderservice.enums.OrderStatus;

public class OrderUpdateDto {

	private OrderStatus status;

	private List<OrderProductDto> products;
	
	public OrderUpdateDto() {
		
	}

	public OrderUpdateDto(OrderStatus status, List<OrderProductDto> products) {
		this.status = status;
		this.products = products;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public List<OrderProductDto> getProducts() {
		return products;
	}

	public void setProducts(List<OrderProductDto> products) {
		this.products = products;
	}

	
	
	
}
