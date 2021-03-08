package com.store.demo.controller;

import com.store.demo.dto.PageableCollection;
import com.store.demo.dto.ResourceIdDto;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Product;
import com.store.demo.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController extends AbstractController
{

	private final ProductService productService;

	public ProductController(final ProductService productService)
	{
		this.productService = productService;
	}

	@GetMapping
	public HttpEntity<PageableCollection<Product>> getProducts(@PageableDefault(size = 20) Pageable pageable)
	{
		final Page<Product> products = productService.getAll(pageable);
		return ResponseEntity.ok(PageableCollection.of(products));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Product> get(@PathVariable final String id)
	{
		final Optional<Product> dto = productService.get(id);
		return dto.map(body -> ResponseEntity.ok().body(body)).orElseThrow(() -> new ResourceNotFoundException(id));
	}

	@PostMapping
	public ResponseEntity<ResourceIdDto> create(@RequestBody @Valid final Product dto)
	{
		final String id = productService.create(dto);
		return ResponseEntity.ok(ResourceIdDto.of(id));
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> update(@PathVariable final String id, @RequestBody @Valid final Product dto)
	{
		productService.update(id, dto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable final String id)
	{
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
