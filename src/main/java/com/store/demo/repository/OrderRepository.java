package com.store.demo.repository;

import com.store.demo.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, String>
{
}
