package com.store.demo.repository;

import com.store.demo.model.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String>
{
	Page<ProductDocument> findByAttributesName(String name, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"match\": {\"attributes.name\": \"?0\"}}]}}")
	Page<ProductDocument> findByAttributesNameUsingCustomQuery(String name, Pageable pageable);

	@Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
	Page<ProductDocument> findByFilteredTagQuery(String tag, Pageable pageable);

	@Query("{\"bool\": {\"must\": {\"match\": {\"attributes.name\": \"?0\"}}, \"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
	Page<ProductDocument> findByAttributesNameAndFilteredTagQuery(String name, String tag, Pageable pageable);
}
