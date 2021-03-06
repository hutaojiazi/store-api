package com.store.demo.service;

import com.store.demo.dto.OrderProductDto;
import com.store.demo.dto.OrderRequestDto;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Order;
import com.store.demo.model.OrderProduct;
import com.store.demo.model.OrderStatus;
import com.store.demo.repository.OrderProductRepository;
import com.store.demo.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService
{
	private final ProductService productService;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

	public OrderServiceImpl(final ProductService productService, final OrderRepository orderRepository,
			final OrderProductRepository orderProductRepository)
	{
		this.productService = productService;
		this.orderRepository = orderRepository;
		this.orderProductRepository = orderProductRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Order> getAll(final Pageable pageable)
	{
		return orderRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Order> get(String id)
	{
		return orderRepository.findById(id);
	}

	@Override
	@Transactional
	public String create(final OrderRequestDto requestDto)
	{
		final List<OrderProductDto> productOrders = requestDto.getProductOrders();
		validateProductsExistence(productOrders);

		final Order order = Order.builder()
				.id(UUID.randomUUID().toString())
				.status(OrderStatus.PAID.name())
				.createdAt(OffsetDateTime.now())
				.build();
		final Order savedOrder = orderRepository.save(order);

		final List<OrderProduct> orderProducts = productOrders.stream().map(dto -> {
			final OrderProduct orderProduct = new OrderProduct(savedOrder, productService.get(dto.getProduct().getId()).get(),
					dto.getQuantity());
			return orderProductRepository.save(orderProduct);
		}).collect(Collectors.toList());

		savedOrder.setOrderProducts(orderProducts);
		orderRepository.save(savedOrder);

		return savedOrder.getId();
	}

	private void validateProductsExistence(final List<OrderProductDto> orderProducts)
	{
		final List<OrderProductDto> list = orderProducts.stream()
				.filter(op -> Objects.isNull(productService.get(op.getProduct().getId())))
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(list))
		{
			new ResourceNotFoundException("Product not found");
		}
	}
}
