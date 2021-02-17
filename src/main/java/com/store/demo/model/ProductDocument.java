package com.store.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "product")
public class ProductDocument
{
	@Id
	private String id;

	@MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = { @InnerField(suffix = "verbatim", type = Keyword) })
	private String name;

	@Field(type = Nested, includeInParent = true)
	private List<Attribute> attributes;

	@Field(type = Keyword)
	private String[] tags;
}
