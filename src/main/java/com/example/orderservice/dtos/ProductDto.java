package com.example.orderservice.dtos;

import java.math.BigDecimal;

public class ProductDto {
	
	private Long id;
	
	private BigDecimal price;
	
	private Long stockQuantity;
	
	public ProductDto() {
		
	}

	public ProductDto(Long id, BigDecimal price, Long stockQuantity) {
		this.id = id;
		this.price = price;
		this.stockQuantity = stockQuantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Long stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	
	
	

}
