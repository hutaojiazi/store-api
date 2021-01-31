package com.store.demo.repository;

import com.store.demo.model.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, String>
{
}
