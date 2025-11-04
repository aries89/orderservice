package com.example.orderservice.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.orderservice.entities.Order;
import com.example.orderservice.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	
	List<Order> findByUserId(Long userId);
	
	Page<Order> findByStatus(OrderStatus status,Pageable pageable);

}
