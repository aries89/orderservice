package com.example.orderservice.dtos;

import jakarta.validation.constraints.NotNull;

public class OrderProductDto {
	
	@NotNull
	private Long productId;
	
	@NotNull
	private Long quantity;
	
	public OrderProductDto() {
		
	}

	public OrderProductDto(Long productId, Long quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	
	
	

}
