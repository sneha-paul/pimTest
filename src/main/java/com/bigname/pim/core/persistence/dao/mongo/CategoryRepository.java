package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Category;
import com.bigname.pim.core.domain.CategoryProduct;
import com.bigname.pim.core.domain.Product;
import com.bigname.pim.core.domain.RelatedCategory;
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
public interface CategoryRepository extends GenericRepository<Category, Criteria> {
    Page<Map<String, Object>> getSubCategories(String categoryId, Pageable pageable, boolean... activeRequired);

    List<RelatedCategory> getAllSubCategories(String categoryId, boolean... activeRequired);

    Page<Category> findAvailableSubCategoriesForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllSubCategories(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> getProducts(String categoryId, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllCategoryProducts(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    List<CategoryProduct> getAllCategoryProducts(String categoryId, boolean... activeRequired);

    Page<Product> findAvailableProductsForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> getAllParentCategoryProducts(String categoryId, Pageable pageable, boolean... activeRequired);

    Page<Map<String, Object>> findAllParentCategoryProducts(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired);
}
