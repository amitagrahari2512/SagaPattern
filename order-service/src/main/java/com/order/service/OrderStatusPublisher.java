package com.order.service;

import org.springframework.stereotype.Service;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.event.OrderEvent;
import com.common.dtos.event.OrderStatus;

import reactor.core.publisher.Sinks;

@Service
public class OrderStatusPublisher {
	
	private Sinks.Many<OrderEvent> orderSinks;
	
	public void publishOrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
		OrderEvent orderEvent = new OrderEvent(orderRequestDto, orderStatus);
		orderSinks.tryEmitNext(orderEvent);
	}
	

}
