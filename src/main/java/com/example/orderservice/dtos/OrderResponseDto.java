package com.example.orderservice.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.orderservice.enums.OrderStatus;

public class OrderResponseDto {
	
	private Long id;

    private Long userId;

    private List<OrderProductDto> products;

    private BigDecimal totalAmount; 

    private OrderStatus status;

    private LocalDateTime orderDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    public OrderResponseDto() {
    	
    }

	public OrderResponseDto(Long id, Long userId, List<OrderProductDto> products, BigDecimal totalAmount, OrderStatus status,
			LocalDateTime orderDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.products = products;
		this.totalAmount = totalAmount;
		this.status = status;
		this.orderDate = orderDate;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
    

}
