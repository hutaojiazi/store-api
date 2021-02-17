package com.store.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "store_orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderProducts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order
{
	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	@Column(name = "status")
	private String status;

	@CreatedDate
	@Column(name = "created_at")
	protected OffsetDateTime createdAt;

	@OneToMany(mappedBy = "pk.order")
	@Valid
	@Builder.Default
	private List<OrderProduct> orderProducts = new ArrayList<>();

	@Transient
	public Double getTotalOrderPrice()
	{
		double sum = 0D;
		List<OrderProduct> orderProducts = getOrderProducts();
		for (OrderProduct op : orderProducts)
		{
			sum += op.getTotalPrice();
		}

		return sum;
	}

	@Transient
	public int getNumberOfProducts()
	{
		return this.orderProducts.size();
	}
}
