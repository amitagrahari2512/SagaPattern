package com.payment.config;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.common.dtos.event.OrderEvent;
import com.common.dtos.event.OrderStatus;
import com.common.dtos.event.PaymentEvent;
import com.payment.service.PaymentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class PaymentConsumerConfig {
	
	@Autowired
	private PaymentService paymentService;
	
	@Bean
	public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor() {
		return orderEventFlux -> orderEventFlux.flatMap(this :: processPayment);
	}

	
	public Mono<PaymentEvent> processPayment(OrderEvent orderEvent) {
		// get the userId
		// check the balance availability
		// if balance sufficient -> Payment completed and deduct amount from DB
		// of payment not sufficient -> cancel order event and update the amount in DB
		
		if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())) {
			return Mono.fromSupplier(() -> this.paymentService.newOrderEvent(orderEvent));
		}
		else {
			return Mono.fromRunnable(() -> this.paymentService.cancelOrderEvent(orderEvent));
		}
	}
}
