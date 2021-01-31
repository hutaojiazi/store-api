package com.store.demo.controller;

import com.store.demo.dto.OrderProductDto;
import com.store.demo.dto.OrderRequestDto;
import com.store.demo.dto.ResourceIdDto;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Order;
import com.store.demo.model.OrderProduct;
import com.store.demo.model.OrderStatus;
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
	private final OrderService orderService;

	public OrderController(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	@GetMapping
	public HttpEntity<Iterable<Order>> getAll()
	{
		return ResponseEntity.ok(orderService.getAll());
	}

	@PostMapping
	public ResponseEntity<ResourceIdDto> create(@RequestBody @Valid final OrderRequestDto requestDto)
	{
		final String id = orderService.create(requestDto);
		return ResponseEntity.created(buildLocationHeader(id)).body(ResourceIdDto.of(id));
	}


}
