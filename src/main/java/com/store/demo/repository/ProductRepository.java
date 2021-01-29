package com.store.demo.repository;

import com.store.demo.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String>
{
}
