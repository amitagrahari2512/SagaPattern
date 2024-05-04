package com.order.config;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.event.OrderStatus;
import com.common.dtos.event.PaymentStatus;
import com.order.entity.PurchaseOrder;
import com.order.repository.OrderRepository;
import com.order.service.OrderStatusPublisher;

@Configuration
public class OrderStatusUpdateHandler {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderStatusPublisher publisher;
	
	
	public void updateOrder(int orderId, Consumer<PurchaseOrder> consumer) {
		 orderRepository.findById(orderId).ifPresent(consumer.andThen(this::updateOrder));
	}
	
	private void updateOrder(PurchaseOrder purchaseOrder) {
		boolean isPaymentComplete = PaymentStatus.PAYMENT_COMPLETED.equals(purchaseOrder.getPaymentStatus());
		OrderStatus orderStatus = isPaymentComplete ? OrderStatus.ORDER_COMPLATED : OrderStatus.ORDER_CANCELLED;
		purchaseOrder.setOrderStatus(orderStatus);
		if(!isPaymentComplete) {
			publisher.publishOrderEvent(convertEntityToDto(purchaseOrder), orderStatus);
		}
	}
	
	public OrderRequestDto convertEntityToDto(PurchaseOrder purchaseOrder) {
		OrderRequestDto orderRequestDto = new OrderRequestDto();
		orderRequestDto.setOrderId(purchaseOrder.getId());
		orderRequestDto.setUserId(purchaseOrder.getUserId());
		orderRequestDto.setAmount(purchaseOrder.getPrice());
		orderRequestDto.setProductId(purchaseOrder.getProductId());
		return orderRequestDto;
	}
}
