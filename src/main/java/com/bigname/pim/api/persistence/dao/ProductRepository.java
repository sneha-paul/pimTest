package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ProductRepository extends GenericRepository<Product> {
    Page<Map<String, Object>> getCategories(String productId, Pageable pageable);
}
