package com.store.demo.dto;

import com.store.demo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductDto
{
	private Product product;
	private Integer quantity;
}
