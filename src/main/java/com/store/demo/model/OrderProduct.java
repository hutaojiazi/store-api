package com.store.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "store_order_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "pk")
public class OrderProduct
{
	@EmbeddedId
	@JsonIgnore
	private OrderProductPK pk;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	public OrderProduct(Order order, Product product, Integer quantity)
	{
		pk = new OrderProductPK();
		pk.setOrder(order);
		pk.setProduct(product);
		this.quantity = quantity;
	}

	@Transient
	public Product getProduct()
	{
		return this.pk.getProduct();
	}

	@Transient
	public Double getTotalPrice()
	{
		return getProduct().getPrice() * getQuantity();
	}
}