package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ProductVariantRepository extends GenericRepository<ProductVariant> {
    Page<ProductVariant> findAll(String searchField, String keyword, String productId, String channelId, Pageable pageable, boolean... activeRequired);

    List<Map<String, Object>> getAll();
}
