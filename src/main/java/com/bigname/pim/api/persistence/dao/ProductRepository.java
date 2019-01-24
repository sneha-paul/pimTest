package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ProductRepository extends GenericRepository<Product> {
    Page<Map<String, Object>> getCategories(String productId, Pageable pageable);

    Page<Map<String, Object>> findAllProductCategories(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<ProductCategory> getAllProductCategories(String productId, boolean... activeRequired);

    Page<Category> findAvailableCategoriesForProduct(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
