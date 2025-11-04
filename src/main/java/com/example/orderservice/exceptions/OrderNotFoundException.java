package com.example.orderservice.exceptions;

public class OrderNotFoundException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8735646865706808043L;

	public OrderNotFoundException(Long orderId) {
		super("Order with id: "+orderId+" not found.");
	}

}
