package com.store.demo.repository;

import com.store.demo.model.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, String>
{
}
