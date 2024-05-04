package com.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.event.OrderStatus;
import com.order.entity.PurchaseOrder;
import com.order.repository.OrderRepository;


public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderStatusPublisher orderStatusPublisher;
	
	@Transactional
	public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
		PurchaseOrder purchaseOrder = orderRepository.save(convertDtoToPurchaseEntity(orderRequestDto));
		orderRequestDto.setOrderId(purchaseOrder.getId());
		//produce kafka event with status ORDER_CREATED
		orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
		return purchaseOrder;
		
	}
	
	private PurchaseOrder convertDtoToPurchaseEntity(OrderRequestDto orderRequestDto) {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setProductId(orderRequestDto.getProductId());
		purchaseOrder.setUserId(orderRequestDto.getUserId());
		purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
		purchaseOrder.setPrice(orderRequestDto.getAmount());
		return purchaseOrder;
		
	}

	public List<PurchaseOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	
}
