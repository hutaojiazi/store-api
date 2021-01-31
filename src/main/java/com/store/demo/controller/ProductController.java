package com.store.demo.controller;

import com.store.demo.dto.ResourceIdDto;
import com.store.demo.model.Product;
import com.store.demo.service.ProductService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController extends AbstractController
{

	private final ProductService productService;

	public ProductController(final ProductService productService)
	{
		this.productService = productService;
	}

	@GetMapping(value = { "", "/" })
	public HttpEntity<Iterable<Product>> getProducts()
	{
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@PostMapping
	public ResponseEntity<ResourceIdDto> create(@RequestBody @Valid final Product dto)
	{
		final String id = productService.save(dto).getId();
		return ResponseEntity.ok(ResourceIdDto.of(id));
	}
}
