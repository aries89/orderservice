package com.example.orderservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.orderservice.dtos.OrderProductDto;
import com.example.orderservice.dtos.OrderRequestDto;
import com.example.orderservice.dtos.OrderResponseDto;
import com.example.orderservice.dtos.OrderUpdateDto;
import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.OrderProduct;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
	
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "status", expression = "java(requestDto.getStatus() != null ? requestDto.getStatus() : OrderStatus.CREATED)")
	Order toEntity(OrderRequestDto requestDto);
	
	OrderResponseDto toDto(Order order);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userId", ignore = true)
	@Mapping(target = "orderDate", ignore = true)
	@Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	void updateOrderFromDto(OrderUpdateDto updateDto,@MappingTarget Order order);
	
	
	OrderProduct toEntity(OrderProductDto orderProductDto);
	
	OrderProductDto toDto(OrderProduct orderProduct);
	
	void updateOrderProductFromDto(OrderProductDto orderProductDto,@MappingTarget OrderProduct orderProduct);

}
