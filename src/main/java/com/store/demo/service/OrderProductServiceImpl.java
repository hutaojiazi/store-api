package com.store.demo.service;

import com.store.demo.model.OrderProduct;
import com.store.demo.repository.OrderProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderProductServiceImpl implements OrderProductService
{

	private final OrderProductRepository orderProductRepository;

	public OrderProductServiceImpl(final OrderProductRepository orderProductRepository)
	{
		this.orderProductRepository = orderProductRepository;
	}

	@Override
	public OrderProduct create(final OrderProduct orderProduct)
	{
		return this.orderProductRepository.save(orderProduct);
	}
}
