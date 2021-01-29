package com.store.demo.service;

import com.store.demo.model.Product;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface ProductService
{

	@NotNull Iterable<Product> getAllProducts();

	Product getProduct(String id);

	Product save(Product product);
}