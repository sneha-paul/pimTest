package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface CategoryRepository extends GenericRepository<Category> {
    Page<Map<String, Object>> getSubCategories(String categoryId, Pageable pageable);

    Page<Map<String, Object>> getProducts(String categoryId, Pageable pageable);
}
