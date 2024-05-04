package com.payment.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.dto.PaymentRequestDto;
import com.common.dtos.event.OrderEvent;
import com.common.dtos.event.PaymentEvent;
import com.common.dtos.event.PaymentStatus;
import com.payment.entity.UserBalance;
import com.payment.entity.UserTransactions;
import com.payment.repository.UserBalanceRepository;
import com.payment.repository.UserTransactionsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

	@Autowired
	private UserBalanceRepository userBalanceRepository;

	@Autowired
	private UserTransactionsRepository userTransactionsRepository;

	@PostConstruct
	public void initUserBalanceInDb() {
		userBalanceRepository
				.saveAll(Stream.of(new UserBalance(101, 5000), new UserBalance(102, 3000), new UserBalance(103, 4200),
						new UserBalance(104, 20000), new UserBalance(105, 999)).collect(Collectors.toList()));
	}

	/**
	 * 
	 * // get the userId // check the balance availability // if balance sufficient
	 * -> Payment completed and deduct amount from DB // of payment not sufficient
	 * -> cancel order event and update the amount in DB
	 */

	@Transactional
	public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
		OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();
		PaymentRequestDto paymentRequestDto = new PaymentRequestDto(orderRequestDto.getUserId(),
				orderRequestDto.getAmount(), orderRequestDto.getOrderId());

		return userBalanceRepository.findById(orderRequestDto.getUserId())
				.filter(ub -> ub.getAmount() > orderRequestDto.getAmount())
				.map(ub -> {
					ub.setAmount(ub.getAmount() - orderRequestDto.getAmount());
					userTransactionsRepository.save(new UserTransactions(orderRequestDto.getOrderId(),
							orderRequestDto.getUserId(), orderRequestDto.getAmount()));
					return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
				}).orElse(new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_FAILED));
	}

	@Transactional
	public void cancelOrderEvent(OrderEvent orderEvent) {
		userTransactionsRepository.findById(orderEvent.getOrderRequestDto().getUserId())
		.ifPresent(ut -> {
			userBalanceRepository.findById(ut.getUserId())
			.ifPresent(ub -> ub.setAmount(ub.getAmount() + ut.getAmount()));
		});
	}

}
