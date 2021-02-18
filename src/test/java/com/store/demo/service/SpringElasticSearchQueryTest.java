package com.store.demo.service;

import com.store.demo.model.Attribute;
import com.store.demo.model.ProductDocument;
import com.store.demo.repository.ProductDocumentRepository;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SpringElasticSearchQueryTest
{
	@Autowired
	private ElasticsearchRestTemplate elasticsearchTemplate;

	@Autowired
	private ProductDocumentRepository productDocumentRepository;

	@Autowired
	private RestHighLevelClient client;

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
				.name("men T-shirt")
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
				.name("men running shoes")
				.attributes(asList(attribute2))
				.tags(new String[] { "new arrival", "men" })
				.build();
		productDocumentRepository.save(product4);
	}

	@AfterEach
	public void cleanup()
	{
		productDocumentRepository.deleteAll();
	}

	@Test
	public void shouldFindDocumentWithName()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				matchQuery("name", "running shoes").operator(Operator.AND)).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));
		assertEquals(1, products.getTotalHits());
	}

	@Test
	public void shouldReturnTotalHitsForIndexAndForDocument()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name", "eye cream")).build();

		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());
		assertEquals("eye cream", products.getSearchHit(0).getContent().getName());
	}

	@Test
	public void givenPartTitle_whenRunMatchQuery_thenDocIsFound()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name", "men")).build();

		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(2, products.getTotalHits());
	}

	@Test
	public void givenFullTitle_whenRunMatchQueryOnVerbatimField_thenDocIsFound()
	{
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name.verbatim", "eye cream")).build();

		SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());

		searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name.verbatim", "eye")).build();

		products = elasticsearchTemplate.search(searchQuery, ProductDocument.class, IndexCoordinates.of("product"));
		assertEquals(0, products.getTotalHits());
	}

	@Test
	public void givenNestedObject_whenQueryByAttributesName_thenFoundProductsByThatAttribute()
	{
		final QueryBuilder builder = nestedQuery("attributes", boolQuery().must(termQuery("attributes.name", "name1")),
				ScoreMode.None);

		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(2, products.getTotalHits());
	}

	@Test
	public void givenAnalyzedQuery_whenMakeAggregationOnTermCount_thenEachTokenCountsSeparately() throws Exception
	{
		final TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags").field("name");

		final SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);
		final SearchRequest searchRequest = new SearchRequest("product").source(builder);

		final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

		final Map<String, Aggregation> results = response.getAggregations().asMap();
		final ParsedStringTerms topTags = (ParsedStringTerms) results.get("top_tags");

		final List<String> keys = topTags.getBuckets()
				.stream()
				.map(MultiBucketsAggregation.Bucket::getKeyAsString)
				.sorted()
				.collect(toList());
		assertEquals(asList("best", "cream", "eye", "men", "product", "running", "shirt", "shoes", "t"), keys);
	}

	@Test
	public void givenNotAnalyzedQuery_whenMakeAggregationOnTermCount_thenEachTermCountsIndividually() throws Exception
	{
		final TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags")
				.field("tags")
				.order(BucketOrder.count(false));

		final SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);
		final SearchRequest searchRequest = new SearchRequest().indices("product").source(builder);

		final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

		final Map<String, Aggregation> results = response.getAggregations().asMap();
		final ParsedStringTerms topTags = (ParsedStringTerms) results.get("top_tags");

		final List<String> keys = topTags.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(toList());
		assertEquals(asList("new arrival", "washable", "best gift", "men", "summer collection"), keys);
	}

	@Test
	public void givenNotExactPhrase_whenUseSlop_thenQueryMatches()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("name", "eye cream").slop(1))
				.build();

		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());
	}

	@Test
	public void givenPhraseWithType_whenUseFuzziness_thenQueryMatches()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				matchQuery("name", "best produt").operator(Operator.AND).fuzziness(Fuzziness.ONE).prefixLength(3)).build();

		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(1, products.getTotalHits());
	}

	@Test
	public void givenMultimatchQuery_whenDoSearch_thenAllProvidedFieldsMatch()
	{
		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(
				multiMatchQuery("men").field("name").field("tags").type(MultiMatchQueryBuilder.Type.BEST_FIELDS)).build();

		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(2, products.getTotalHits());
	}

	@Test
	public void givenBoolQuery_whenQueryByAttributesName_thenFoundProductsByThatAttributeAndFilteredTag()
	{
		final QueryBuilder builder = boolQuery().must(
				nestedQuery("attributes", boolQuery().must(termQuery("attributes.name", "name2")), ScoreMode.None))
				.filter(termQuery("tags", "new arrival"));

		final NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
		final SearchHits<ProductDocument> products = elasticsearchTemplate.search(searchQuery, ProductDocument.class,
				IndexCoordinates.of("product"));

		assertEquals(2, products.getTotalHits());
	}
}
