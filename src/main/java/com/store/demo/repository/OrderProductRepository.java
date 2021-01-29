package com.store.demo.repository;

import com.store.demo.model.OrderProduct;
import com.store.demo.model.OrderProductPK;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProduct, OrderProductPK>
{
}
