package com.store.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.demo.model.Contact;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ElasticSearchTest
{
	private ObjectMapper objectMapper = new ObjectMapper();
	private List<Contact> contacts = new ArrayList<>();
	private RestHighLevelClient client = null;

	@BeforeEach
	public void setup()
	{
		Contact c1 = Contact.builder().age(10).fullName("John Doe").dateOfBirth(new Date()).build();
		Contact c2 = Contact.builder().age(25).fullName("Janette Doe").dateOfBirth(new Date()).build();
		contacts.add(c1);
		contacts.add(c2);

		ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").build();
		client = RestClients.create(clientConfiguration).rest();
	}

	@Test
	void givenJsonString_whenJavaObject_thenIndexDocument() throws IOException
	{
		String jsonObject = "{\"age\":20,\"dateOfBirth\":1471466076564,\"fullName\":\"John Doe\"}";
		IndexRequest request = new IndexRequest("contact");
		request.source(jsonObject, XContentType.JSON);

		IndexResponse response = client.index(request, RequestOptions.DEFAULT);
		String index = response.getIndex();
		long version = response.getVersion();

		assertEquals(Result.CREATED, response.getResult());
		assertEquals(1, version);
		assertEquals("contact", index);
	}

	@Test
	void givenDocumentId_whenJavaObject_thenDeleteDocument() throws IOException
	{
		String jsonObject = "{\"age\":10,\"dateOfBirth\":1471455886564,\"fullName\":\"Johan Doe\"}";
		IndexRequest indexRequest = new IndexRequest("contact");
		indexRequest.source(jsonObject, XContentType.JSON);

		IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
		String id = response.getId();

		GetRequest getRequest = new GetRequest("contact");
		getRequest.id(id);

		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println(getResponse.getSourceAsString());

		DeleteRequest deleteRequest = new DeleteRequest("contact");
		deleteRequest.id(id);

		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

		assertEquals(Result.DELETED, deleteResponse.getResult());
	}

	@Test
	void givenSearchRequest_whenMatchAll_thenReturnAllResults() throws IOException
	{
		SearchRequest searchRequest = new SearchRequest("contact");
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] searchHits = response.getHits().getHits();
		List<Contact> results = Arrays.stream(searchHits).map(hit -> {
			try
			{
				return objectMapper.readValue(hit.getSourceAsString(), Contact.class);
			}
			catch (JsonProcessingException e)
			{
				System.out.println("json parse failed: " + e.toString());
			}
			return null;
		}).collect(Collectors.toList());

		results.forEach(System.out::println);
	}

	@Test
	void givenSearchParameters_thenReturnResults() throws IOException
	{
		SearchSourceBuilder builder = new SearchSourceBuilder().postFilter(QueryBuilders.rangeQuery("age").from(5).to(15));

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequest.source(builder);

		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

		builder = new SearchSourceBuilder().postFilter(QueryBuilders.simpleQueryStringQuery("+John -Doe OR Janette"));

		searchRequest = new SearchRequest();
		searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequest.source(builder);

		SearchResponse response2 = client.search(searchRequest, RequestOptions.DEFAULT);

		builder = new SearchSourceBuilder().postFilter(QueryBuilders.matchQuery("John", "Name*"));
		searchRequest = new SearchRequest();
		searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequest.source(builder);

		SearchResponse response3 = client.search(searchRequest, RequestOptions.DEFAULT);

		response2.getHits();
		response3.getHits();

		final List<Contact> results = Stream.of(response.getHits().getHits(), response2.getHits().getHits(),
				response3.getHits().getHits()).flatMap(Arrays::stream).map(hit -> {
			try
			{
				return objectMapper.readValue(hit.getSourceAsString(), Contact.class);
			}
			catch (JsonProcessingException e)
			{
				//
			}
			return null;
		}).collect(Collectors.toList());

		results.forEach(System.out::println);
	}

	@Test
	void givenContentBuilder_whenHelpers_thanIndexJson() throws IOException
	{
		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject()
				.field("fullName", "Test")
				.field("salary", "11500")
				.field("age", "10")
				.endObject();

		IndexRequest indexRequest = new IndexRequest("contact");
		indexRequest.source(builder);

		IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);

		assertEquals(Result.CREATED, response.getResult());
	}
}
