package com.store.demo.service;

import com.store.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface ProductService {

    /**
     * Returns the requested page of products
     *
     * @param pageable the page request criteria.
     * @return the requested products page
     */
    Page<Product> getAll(Pageable pageable);

    /**
     * Retrieves a product with provided id.
     *
     * @param id the resource identifier.
     * @return the requested product, or {@link Optional#empty()} if the resource is not found.
     */
    Optional<Product> get(String id);

    /**
     * Creates a new product.
     *
     * @param product
     * @return the id of the product created.
     */
    String create(Product product);

    /**
     * Updates a product with the provided id.
     *
     * @param id      the resource identifier.
     * @param product the product.
     */
    void update(String id, Product product);

    /**
     * Deletes a product by the provided id.
     *
     * @param id the resource identifier
     */
    void delete(String id);
}