package com.example.orderservice.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.orderservice.dtos.OrderProductDto;
import com.example.orderservice.dtos.OrderRequestDto;
import com.example.orderservice.dtos.OrderResponseDto;
import com.example.orderservice.dtos.OrderUpdateDto;
import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.OrderProduct;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.exceptions.OrderNotFoundException;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.repos.OrderRepository;
import com.example.orderservice.services.OrderService;
import com.example.orderservice.webclient.ProductClient;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepo;

	private final OrderMapper orderMapper;

	private final ProductClient productClient;

	public OrderServiceImpl(OrderRepository orderRepo, OrderMapper orderMapper, ProductClient productclient) {
		this.orderMapper = orderMapper;
		this.orderRepo = orderRepo;
		this.productClient = productclient;
	}

	@Override
	public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
		List<OrderProductDto> products = orderRequestDto.getProducts();

		long numberOfProductsAvailable = products.stream()
				.filter(p -> productClient.isAvailable(p.getProductId(), p.getQuantity())).count();

		Order orderEntity = orderMapper.toEntity(orderRequestDto);

		if (products.size() == numberOfProductsAvailable) {

			// TODO Change to Batch API call
			BigDecimal totalAmount = products.stream().map(p -> productClient.getProductById(p.getProductId())
					.getPrice().multiply(BigDecimal.valueOf(p.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
			orderEntity.setTotalAmount(totalAmount);
			Order order = orderRepo.save(orderEntity);

			boolean allStockReduced = true;
			List<OrderProductDto> successfullyReduced = new ArrayList<>();
			for (OrderProductDto product : products) {
				boolean success = productClient.reduceStock(product.getProductId(), product.getQuantity());
				if (!success) {
					allStockReduced = false;
					break;

				}
				successfullyReduced.add(product);
			}

			if (!allStockReduced) {
				order.setTotalAmount(BigDecimal.ZERO);
				order.setStatus(OrderStatus.FAILED);
				for (OrderProductDto product : successfullyReduced) {
					productClient.restoreStock(product.getProductId(), product.getQuantity());
				}
			}

			return orderMapper.toDto(order);
		} else {
			orderEntity.setTotalAmount(BigDecimal.ZERO);
			orderEntity.setStatus(OrderStatus.FAILED);
			Order order = orderRepo.save(orderEntity);
			return orderMapper.toDto(order);
		}
	}

	@Override
	public OrderResponseDto updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
		Order order = orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		List<OrderProduct> productsFromDb = order.getProducts();
		orderMapper.updateOrderFromDto(orderUpdateDto, order);
		for (OrderProductDto productDto : orderUpdateDto.getProducts()) {
			Optional<OrderProduct> orderProduct = productsFromDb.stream()
					.filter(p -> p.getProductId().equals(productDto.getProductId())).findFirst();
			if (orderProduct.isPresent()) {
				OrderProduct existingProduct = orderProduct.get();
				Long diff = productDto.getQuantity() - existingProduct.getQuantity();
				orderMapper.updateOrderProductFromDto(productDto, existingProduct);
				if(diff > 0) {
				  productClient.reduceStock(productDto.getProductId(), diff);
				}else {
			      productClient.restoreStock(productDto.getProductId(), -diff);
				}
			} else {
				OrderProduct op = new OrderProduct();
				orderMapper.updateOrderProductFromDto(productDto, op);
				order.addProduct(op);
				productClient.reduceStock(productDto.getProductId(), productDto.getQuantity());
			}
		}
		
		BigDecimal totalAmount = order.getProducts().stream().map(p -> productClient.getProductById(p.getProductId())
				.getPrice().multiply(BigDecimal.valueOf(p.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalAmount(totalAmount);
		return orderMapper.toDto(order);
	}

	@Override
	public OrderResponseDto getOrderById(Long id) {
		return orderMapper.toDto(this.orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id)));
	}

	@Override
	public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
		Page<Order> orders = orderRepo.findAll(pageable);
		return orders.map(o -> orderMapper.toDto(o));
	}

	@Override
	public void deleteOrder(Long id) {
		if (orderRepo.existsById(id)) {
			orderRepo.deleteById(id);
		} else {
			throw new OrderNotFoundException(id);
		}

	}

	@Override
	public List<OrderResponseDto> getOrdersByUserId(Long userId) {
		List<Order> orders = orderRepo.findByUserId(userId);
		return orders.stream().map(o -> orderMapper.toDto(o)).toList();
	}

	@Override
	public Page<OrderResponseDto> getOrdersByStatus(String status, Pageable pageable) {
		Page<Order> orders = orderRepo.findByStatus(OrderStatus.valueOf(status), pageable);
		return orders.map(orderMapper::toDto);
	}

}
