package com.store.demo.service;

import com.store.demo.dto.OrderRequestDto;
import com.store.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface OrderService
{
	/**
	 * Returns the requested page of orders
	 *
	 * @param pageable the page request criteria.
	 * @return the requested orders page
	 */
	Page<Order> getAll(Pageable pageable);

	/**
	 * Retrieves an order with provided id.
	 *
	 * @param id the resource identifier.
	 * @return the requested order, or {@link Optional#empty()} if the resource is not found.
	 */
	Optional<Order> get(String id);

	/**
	 * creates a new order.
	 *
	 * @param requestDto
	 * @return the id of the order created.
	 */
	String create(OrderRequestDto requestDto);
}
