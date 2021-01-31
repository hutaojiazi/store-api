package com.store.demo.controller;

import com.store.demo.dto.OrderRequestDto;
import com.store.demo.dto.PageableCollection;
import com.store.demo.dto.ResourceIdDto;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Order;
import com.store.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

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
	public HttpEntity<PageableCollection<Order>> getAll(@PageableDefault(size = 20) Pageable pageable)
	{
		final Page<Order> orders = orderService.getAll(pageable);
		return ResponseEntity.ok(PageableCollection.of(orders));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Order> get(@PathVariable final String id)
	{
		final Optional<Order> dto = orderService.get(id);
		return dto.map(body -> ResponseEntity.ok().body(body)).orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@PostMapping
	public ResponseEntity<ResourceIdDto> create(@RequestBody @Valid final OrderRequestDto requestDto)
	{
		final String id = orderService.create(requestDto);
		return ResponseEntity.created(buildLocationHeader(id)).body(ResourceIdDto.of(id));
	}

}
