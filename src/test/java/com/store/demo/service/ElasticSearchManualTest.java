package com.store.demo.service;

import com.store.demo.config.ElasticSearchConfig;
import com.store.demo.model.Attribute;
import com.store.demo.model.ProductDocument;
import com.store.demo.repository.ProductDocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ElasticSearchConfig.class)
public class ElasticSearchManualTest
{
	@Autowired
	private ElasticsearchRestTemplate elasticsearchTemplate;

	@Autowired
	private ProductDocumentRepository productDocumentRepository;

	private final Attribute attribute1 = Attribute.builder().name("attribute name1").build();
	private final Attribute attribute2 = Attribute.builder().name("attribute name2").build();

	@BeforeEach
	public void setup()
	{
		final ProductDocument product1 = ProductDocument.builder()
				.name("best product")
				.attributes(asList(attribute1, attribute2))
				.tags(new String[] { "washable", "new arrival" })
				.build();
		productDocumentRepository.save(product1);

		final ProductDocument product2 = ProductDocument.builder()
				.name("men's T-shirt")
				.attributes(asList(attribute2))
				.tags(new String[] { "best gift", "summer collection" })
				.build();
		productDocumentRepository.save(product2);

		final ProductDocument product3 = ProductDocument.builder()
				.name("eye cream")
				.attributes(asList(attribute1))
				.tags(new String[] { "washable", "new arrival" })
				.build();
		productDocumentRepository.save(product3);

		final ProductDocument product4 = ProductDocument.builder()
				.name("running shoes")
				.attributes(asList(attribute2))
				.tags(new String[] { "new arrival" })
				.build();
		productDocumentRepository.save(product4);
	}

	@AfterEach
	public void cleanup()
	{
		productDocumentRepository.deleteAll();
	}

	@Test
	public void shouldReturnProductDocumentIdAfterSave()
	{
		final List<Attribute> attributes = asList(attribute1, attribute2);
		final ProductDocument product = ProductDocument.builder().name("product1").attributes(attributes).build();

		final ProductDocument entity = productDocumentRepository.save(product);
		assertNotNull(entity.getId());
	}

	@Test
	public void shouldReturnProductDocumentFoundByAttributes()
	{
		final Page<ProductDocument> productPage = productDocumentRepository.findByAttributesName(attribute1.getName(),
				PageRequest.of(0, 10));
		assertEquals(2, productPage.getTotalElements());
	}

	@Test
	public void shouldReturnProductDocumentFoundByAttributesCustomQuery()
	{
		final Page<ProductDocument> productByAttributeName = productDocumentRepository.findByAttributesNameUsingCustomQuery("name1",
				PageRequest.of(0, 10));
		assertEquals(2, productByAttributeName.getTotalElements());
	}

	@Test
	public void shouldReturnProductDocumentFoundByTag()
	{
		final Page<ProductDocument> productByAttributeName = productDocumentRepository.findByFilteredTagQuery("washable",
				PageRequest.of(0, 10));
		assertEquals(2, productByAttributeName.getTotalElements());
	}

	@Test
	public void shouldReturnProductDocumentFoundByAttributesAndTag()
	{
		final Page<ProductDocument> productByAttributeName = productDocumentRepository.findByAttributesNameAndFilteredTagQuery("name2",
				"washable", PageRequest.of(0, 10));
		assertEquals(1, productByAttributeName.getTotalElements());
	}

	@Test
	public void shouldReturnProductDocumentFoundByRegexQuery()
	{
		final Query searchQuery = new NativeSearchQueryBuilder().withFilter(regexpQuery("name", ".*cream.*")).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());
	}

	@Test
	public void shouldReturnProductDocumentFoundByFuzzyQuery()
	{
		final Query searchQuery = new NativeSearchQueryBuilder().withQuery(fuzzyQuery("name", "running")).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());

		final ProductDocument product = products.getSearchHit(0).getContent();
		final String name = "Yoga pants";
		product.setName(name);
		productDocumentRepository.save(product);

		assertEquals(name, productDocumentRepository.findById(product.getId()).get().getName());
	}

	@Test
	public void shouldReturnProductDocumentFoundByMatchQuery()
	{
		final String productName = "best product";
		final Query searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name", productName).minimumShouldMatch("75%"))
				.build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());

		final long count = productDocumentRepository.count();
		productDocumentRepository.delete(products.getSearchHit(0).getContent());

		assertEquals(count - 1, productDocumentRepository.count());
	}

	@Test
	public void shouldDecreaseTotalHitsCount()
	{
		final Query searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name", "men's T-shirt").operator(AND)).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));
		assertEquals(1, products.getTotalHits());
	}
}
