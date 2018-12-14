package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface CatalogRepository extends GenericRepository<Catalog> {
    Page<Map<String, Object>> getAllRootCategories(String catalogId, Pageable pageable);
}
