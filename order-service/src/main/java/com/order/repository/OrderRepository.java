package com.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.entity.PurchaseOrder;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Integer>{

}
