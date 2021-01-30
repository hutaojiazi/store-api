package com.store.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "store_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product
{
	@Id
	private String id = UUID.randomUUID().toString();

	@NotNull(message = "Product name is required.")
	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private Double price;

	@Column(name = "url")
	private String pictureUrl;
}
