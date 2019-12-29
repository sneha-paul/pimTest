package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Category;
import com.bigname.pim.core.domain.Product;
import com.bigname.pim.core.domain.ProductCategory;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ProductRepository extends GenericRepository<Product, Criteria> {
    Page<Map<String, Object>> getCategories(String productId, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllProductCategories(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<ProductCategory> getAllProductCategories(String productId, boolean... activeRequired);

    Page<Category> findAvailableCategoriesForProduct(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
