package com.example.orderservice.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.orderservice.dtos.OrderProductDto;
import com.example.orderservice.dtos.OrderRequestDto;
import com.example.orderservice.dtos.OrderResponseDto;
import com.example.orderservice.dtos.OrderUpdateDto;
import com.example.orderservice.dtos.ProductDto;
import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.OrderProduct;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.exceptions.OrderNotFoundException;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.repos.OrderRepository;
import com.example.orderservice.services.OrderService;
import com.example.orderservice.webclient.ProductClient;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

			List<Long> ids = products.stream().map(OrderProductDto::getProductId).toList();
			Map<Long, BigDecimal> productPriceMap = productClient.getProductsByIds(ids).stream()
					.collect(Collectors.toMap(ProductDto::getId, ProductDto::getPrice));
			BigDecimal totalAmount = products.stream()
					.map(p -> productPriceMap.get(p.getProductId()).multiply(BigDecimal.valueOf(p.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
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
		List<OrderProduct> productsFromDB = order.getProducts();
		HashMap<Long, Long> stockAdjustments = new HashMap<>();
		try {
			for (OrderProductDto productDto : orderUpdateDto.getProducts()) {
				Optional<OrderProduct> orderProduct = productsFromDB.stream()
						.filter(p -> p.getProductId().equals(productDto.getProductId())).findFirst();
				if (orderProduct.isPresent()) {
					OrderProduct existingProduct = orderProduct.get();
					Long diff = productDto.getQuantity() - existingProduct.getQuantity();
					orderMapper.updateOrderProductFromDto(productDto, existingProduct);
					if (diff > 0) {
						productClient.reduceStock(productDto.getProductId(), diff);
					} else {
						productClient.restoreStock(productDto.getProductId(), -diff);
					}
					stockAdjustments.put(productDto.getProductId(), diff);
				} else {
					OrderProduct op = new OrderProduct();
					orderMapper.updateOrderProductFromDto(productDto, op);
					order.addProduct(op);
					productClient.reduceStock(productDto.getProductId(), productDto.getQuantity());
					stockAdjustments.put(productDto.getProductId(), productDto.getQuantity());
				}
			}
			orderMapper.updateOrderFromDto(orderUpdateDto, order);
			List<Long> ids = productsFromDB.stream().map(OrderProduct::getProductId).toList();
			Map<Long, BigDecimal> productPriceMap = productClient.getProductsByIds(ids).stream()
					.collect(Collectors.toMap(ProductDto::getId, ProductDto::getPrice));
			BigDecimal totalAmount = productsFromDB.stream()
					.map(p -> productPriceMap.get(p.getProductId()).multiply(BigDecimal.valueOf(p.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			
			order.setTotalAmount(totalAmount);
			order = orderRepo.save(order);
			return orderMapper.toDto(order);

		} catch (Exception e) {

			stockAdjustments.forEach((productId, quantity) -> {
				try {
					if (quantity > 0) {
						productClient.restoreStock(productId, quantity);
					} else {
						productClient.reduceStock(productId, -quantity);
					}
				} catch (Exception ex) {
					log.error("Failed to revert stock for product : {} : {}", productId, ex);
				}

			});
			throw new RuntimeException("Failed to update the order :" + id, e);
		}
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
		Page<Order> orders = orderRepo.findByStatus(OrderStatus.valueOf(status.toUpperCase()), pageable);
		return orders.map(orderMapper::toDto);
	}

}
