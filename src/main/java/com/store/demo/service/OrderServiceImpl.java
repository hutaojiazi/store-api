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

	private OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository)
	{
		this.orderRepository = orderRepository;
	}

	@Override
	public Iterable<Order> getAllOrders()
	{
		return this.orderRepository.findAll();
	}

	@Override
	public Order create(Order order)
	{
		order.setCreatedAt(OffsetDateTime.now());

		return this.orderRepository.save(order);
	}

	@Override
	public void update(Order order)
	{
		this.orderRepository.save(order);
	}
}
