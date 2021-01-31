package com.store.demo.dto;

import com.store.demo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductDto
{
	@NotNull
	private Product product;

	@NotNull
	@Min(value = 1)
	private Integer quantity;
}
