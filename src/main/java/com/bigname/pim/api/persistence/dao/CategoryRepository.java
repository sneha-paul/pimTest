package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.RelatedCategory;
import com.m7.xcore.persistence.dao.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface CategoryRepository extends GenericRepository<Category> {
    Page<Map<String, Object>> getSubCategories(String categoryId, Pageable pageable);

    List<RelatedCategory> getAllSubCategories(String categoryId, boolean... activeRequired);

    Page<Category> findAvailableSubCategoriesForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllSubCategories(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> getProducts(String categoryId, Pageable pageable);

    Page<Map<String, Object>> findAllCategoryProducts(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<CategoryProduct> getAllCategoryProducts(String categoryId, boolean... activeRequired);

    Page<Product> findAvailableProductsForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
