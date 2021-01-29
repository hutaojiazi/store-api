package com.store.demo.service;

import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Product;
import com.store.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService
{

	private ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository)
	{
		this.productRepository = productRepository;
	}

	@Override
	public Iterable<Product> getAllProducts()
	{
		return productRepository.findAll();
	}

	@Override
	public Product getProduct(String id)
	{
		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
	}

	@Override
	public Product save(Product product)
	{
		return productRepository.save(product);
	}
}