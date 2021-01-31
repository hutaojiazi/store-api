package com.store.demo.service;

import com.store.demo.dto.OrderRequestDto;
import com.store.demo.model.Order;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface OrderService
{
	@NotNull Iterable<Order> getAll();

	/**
	 * creates a new order.
	 * @param requestDto
	 * @return
	 */
	String create(OrderRequestDto requestDto);
}
