package com.store.demo.controller;

import com.store.demo.model.Product;
import com.store.demo.service.ProductService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
