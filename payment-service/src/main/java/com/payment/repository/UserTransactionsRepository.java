package com.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.entity.UserTransactions;

public interface UserTransactionsRepository extends JpaRepository<UserTransactions, Integer>{

}
