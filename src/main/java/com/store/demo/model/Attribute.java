package com.store.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute
{
	@Field(type = Text)
	private String name;
}
