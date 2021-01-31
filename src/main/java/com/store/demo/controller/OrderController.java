package com.store.demo.controller;

import com.store.demo.dto.OrderProductDto;
import com.store.demo.dto.OrderRequestDto;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Order;
import com.store.demo.model.OrderProduct;
import com.store.demo.model.OrderStatus;
import com.store.demo.service.OrderProductService;
import com.store.demo.service.OrderService;
import com.store.demo.service.ProductService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController extends AbstractController
{
	private final ProductService productService;
	private final OrderService orderService;
	private final OrderProductService orderProductService;

	public OrderController(final ProductService productService, final OrderService orderService,
			final OrderProductService orderProductService)
	{
		this.productService = productService;
		this.orderService = orderService;
		this.orderProductService = orderProductService;
	}

	@GetMapping
	public HttpEntity<Iterable<Order>> getAll()
	{
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@PostMapping
	public ResponseEntity<Order> create(@RequestBody @Valid final OrderRequestDto form)
	{
		final List<OrderProductDto> formDtos = form.getProductOrders();
		validateProductsExistence(formDtos);

		final Order order = Order.builder().id(UUID.randomUUID().toString()).status(OrderStatus.PAID.name()).build();
		final Order savedOrder = orderService.create(order);

		final List<OrderProduct> orderProducts = formDtos.stream().map(dto -> {
			final OrderProduct orderProduct = new OrderProduct(savedOrder, productService.getProduct(dto.getProduct().getId()),
					dto.getQuantity());
			return orderProductService.create(orderProduct);
		}).collect(Collectors.toList());

		savedOrder.setOrderProducts(orderProducts);
		orderService.update(savedOrder);

		return ResponseEntity.created(buildLocationHeader(savedOrder.getId())).body(savedOrder);
	}

	private void validateProductsExistence(final List<OrderProductDto> orderProducts)
	{
		final List<OrderProductDto> list = orderProducts.stream()
				.filter(op -> Objects.isNull(productService.getProduct(op.getProduct().getId())))
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(list))
		{
			new ResourceNotFoundException("Product not found");
		}
	}
}
