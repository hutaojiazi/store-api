package com.store.demo.service;

import com.store.demo.model.Order;
import com.store.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class OrderServiceImpl implements OrderService
{

	private final OrderRepository orderRepository;

	public OrderServiceImpl(final OrderRepository orderRepository)
	{
		this.orderRepository = orderRepository;
	}

	@Override
	public Iterable<Order> getAllOrders()
	{
		return this.orderRepository.findAll();
	}

	@Override
	public Order create(final Order order)
	{
		order.setCreatedAt(OffsetDateTime.now());
		return orderRepository.save(order);
	}

	@Override
	public void update(final Order order)
	{
		orderRepository.save(order);
	}
}
